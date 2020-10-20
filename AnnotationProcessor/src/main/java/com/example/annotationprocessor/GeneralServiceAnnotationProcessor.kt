package com.example.annotationprocessor

import com.example.annotations.GeneralServiceGenerator
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import kastree.ast.MutableVisitor
import kastree.ast.Node
import kastree.ast.Writer
import kastree.ast.psi.Converter
import kastree.ast.psi.Parser
import org.jetbrains.kotlin.spec.grammar.tools.*
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.FileObject
import javax.tools.StandardLocation
import javax.tools.ToolProvider


@AutoService(Processor::class)
class GeneralServiceAnnotationProcessor : AbstractProcessor(){

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        val stringClassName = String::class.asClassName()

        val queryAnnotationName = ClassName("retrofit2.http", "Query")
        val getAnnotationName = ClassName("retrofit2.http", "GET")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(GeneralServiceGenerator::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
//        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, roundEnv.rootElements.toString())
        roundEnv.getElementsAnnotatedWith(GeneralServiceGenerator::class.java)
            .forEach {
                if (it.kind != ElementKind.CLASS) {
                    processingEnv.messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Only classes can be annotated"
                    )
                    return true
                }
                processAnnotation(it)
            }
        return false
    }

    private fun createQuery(query: String) = AnnotationSpec.builder(queryAnnotationName)
        .addMember("%S", query)
        .build()

    private fun getFile() : String{
        val file = File("app/src/main/java/com/example/simpel/GeneralMockService.kt")
        val fileSource = file.bufferedReader().readLines()
        return fileSource.joinToString { it }
    }

    fun Element.hello(): String {
        return "hello"
    }



    private fun processAnnotation(element: Element) {
        val hello = element.hello()

        val className = element.simpleName.toString()

        val pack = processingEnv.elementUtils.getPackageOf(element).toString()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]

        val fileName = "Generated$className"
        val fileBuilder = FileSpec.builder(pack, fileName)
        val interfaceBuilder = TypeSpec.interfaceBuilder(fileName)

        val packbb = processingEnv.elementUtils.getPackageOf(element).qualifiedName.toString()
        val options = processingEnv.options.toString()
        val filer = processingEnv.filer
        val elementPath = "$element".replace('.', '/') + ".kt"

//        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, element.toString() + "\r\n")
//        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, packbb)


        val source = File("app/src/main/java/$elementPath").readLines().joinToString("\n")
        //Only top level classes can be used as a base for a ParsableClass
        //I can use getEnclosingElement to be able to traverse up the element to get to it's ParsableClass
        //Note if for what ever reason the top level class name is not the same as the file name (e.g Sealed Class) Then a ParsableClass(FileName) needs to be used on said element
        //This is because the processingEnv doesn't present the file directly to the annotation processor so we have to open it up manually
        //This works well in the trivial case where the top level class and file share the same name, as we can use a File object to read a file by project + package + filename path
        //How ever if the top level Class is different to the filename this operation above will fail, hence an annotation will need to explicitly declare the filename.

        //So I can create an extension function for each type of element to let them convert itself to KotlinPoet
        //By default it will just convert the element one for one into KotlinPoet
        //However you can override any part of the conversation by first using an Enum to declare the part you want to overwrite, then supplying your own method or null
        //This will then combine the following and spit out the element you need to build


        val pb = PropertySpec.builder("pb", String::class.asTypeName())



//        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, source)
        //make an extention function of Element that
        // 1) checks if top level class by searching for file, and returns a ParsableClass <- can also get this by annotation processing
        // 2) Then call method on parsable class to get a val assignment by name
        // 3) Cancel This can be done by first keeping track of open and closed parenthesis
        // 4) Cancel Or by keeping track of indentation
        // 5) Cancel Or by only accepting a single line for a string (This is what I will choose, and I will through an exception if it doesn't conform, Complex matcher doesn't exist)
        // 6) Scratch that I can actually use this great library to accurately pin point the items I need. Just pass an array of (Type,Name) pairs and the final item will be what is returned.
        // 7) Any item in the chain can be returned, and the last item in the chain can be modified and sent back to the chain.
        // 8) To be able to do this code generation trick with no top level classes and elements, a @File() annotation needs to be used.
        // 9) Also to be able to use this with build flavours other than main then @Flavour will need to be used.

        Converter
        val l = mutableListOf<String>()
        val take2 = Parser().parseFile(source)
        val take3 = MutableVisitor.preVisit(take2) { v, p ->
            v?.tag = p
            v?.let{l.add(it.toString())}

            if (v is Node.Decl.Property) {
//                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, Writer.write(v) + "\r\n")
//                v.expr?.let{processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, Writer.write(it) + "\r\n")}
//                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE,v.toString() + "\r\n")
                v
            }
            else {
//                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, v?.let { Writer.write(it) } + "\n")
                v
            }
        }

        val outtake = Writer.write(take2)

        var resource : FileObject? = null
        var resourceValue : CharSequence = "2"
        var tokens : KotlinTokensList? = null
        var parsed : KotlinParseTree? = null
        try {
            tokens = tokenizeKotlinCode(source)
            parsed = parseKotlinCode(tokenizeKotlinCode(source))
//            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, tokens.toString())
//            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, parsed.type.toString())
//            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, parsed.toString())
        } catch (e: Exception) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, e.toString())
        }

        for (enclosed in element.enclosedElements){
            if (enclosed.kind == ElementKind.FIELD && enclosed.simpleName.toString() == "path"){
                val p = processingEnv.elementUtils.getPackageOf(enclosed)
                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, enclosed.toProperty(processingEnv).toString() + "\r\n")
                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, enclosed.toProperty(processingEnv)?.toExpression() ?: "\r\n")

//                processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, enclosed.enclosingElement.simpleName.toString() + enclosed.enclosingElement.kind + "\r\n")
                interfaceBuilder.addFunction(
                    FunSpec.builder("Generated${enclosed.simpleName}")
                        .addStatement("%S", l.joinToString("\n"))
                        .build()
                )
            }
            if (enclosed.kind == ElementKind.CLASS){
                val bb = enclosed as ExecutableElement
            }
        }

        val file = fileBuilder.addType(interfaceBuilder.build()).build()
        kaptKotlinGeneratedDir?.let{file.writeTo(File(it))}
    }
}


//val types = processingEnv.typeUtils
//val processed = mutableSetOf<String>()
//
//val superClass = types.asElement(types.directSupertypes(element.asType())[0]).simpleName