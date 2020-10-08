package com.example.annotationprocessor

import com.example.annotations.GeneralServiceGenerator
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*

import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class GeneralServiceAnnotationProcessor : AbstractProcessor(){

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        val stringClassName = String::class.asClassName()

        val queryAnnotationName = ClassName("retrofit2.http","Query")
        val getAnnotationName = ClassName("retrofit2.http","GET")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(GeneralServiceGenerator::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(GeneralServiceGenerator::class.java)
            .forEach {
                if (it.kind != ElementKind.CLASS) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
                    return true
                }
                processAnnotation(it)
            }
        return false
    }

    private fun createQuery(query: String) = AnnotationSpec.builder(queryAnnotationName)
        .addMember("%S",query)
        .build()

    private fun getFile() : String{
        val file = File("app/src/main/java/com/example/simpel/GeneralMockService.kt")
        val fileSource = file.bufferedReader().readLines()
        return fileSource.joinToString { it }
    }

    private fun processAnnotation(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]

        val fileName = "Generated$className"
        val fileBuilder = FileSpec.builder(pack, fileName)
        val interfaceBuilder = TypeSpec.interfaceBuilder(fileName)



        for (enclosed in element.enclosedElements){
            if (enclosed.kind == ElementKind.FIELD && enclosed.simpleName.toString() == "path"){
                interfaceBuilder.addFunction(
                    FunSpec.builder("Generated${enclosed.simpleName}")
                        .addStatement("val source = %S", getFile())
                        .build()
                )
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