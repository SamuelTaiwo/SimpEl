package com.example.annotationprocessor

import com.example.annotations.RetrofitAnnotation
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
class RetrofitAnnotationProcessor : AbstractProcessor(){

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        val stringClassName = String::class.asClassName()

        val okHttpClassName = ClassName("okhttp3","OkHttpClient")
        val gsonConverterClassName = ClassName("retrofit2.converter.gson","GsonConverterFactory")
        val coroutineCallAdapterClassName = ClassName("com.jakewharton.retrofit2.adapter.kotlin.coroutines","CoroutineCallAdapterFactory")
        val retrofitClassName = ClassName("retrofit2","Retrofit")
        val generatedMockServiceClassName = ClassName("com.example.simpel","GeneratedMockService")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(RetrofitAnnotation::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(RetrofitAnnotation::class.java)
            .forEach {
                if (it.kind != ElementKind.CLASS) {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated")
                    return true
                }
                processAnnotation(it)
            }
        return false
    }

    private fun processAnnotation(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()

        val fileName = "Generated$className"
        val fileBuilder = FileSpec.builder(pack, fileName)
        val classBuilder = TypeSpec.classBuilder(fileName)

        val nameAllocator = NameAllocator()
        val properties = mutableListOf<PropertySpec>()

        val baseURLName = "BASE_URL"
        val baseURL = PropertySpec.builder(baseURLName, stringClassName)
            .initializer("%S","http://ws.audioscrobbler.com/")
            .addModifiers(KModifier.CONST)
            .build()
            .also{properties.add(it)}

        val okHttpClientName = "okHttpClient"
        val okHttpClient = PropertySpec.builder(okHttpClientName, okHttpClassName)
            .initializer("%T.Builder().build()", okHttpClassName)
            .build()
            .also{properties.add(it)}

        val gsonConverterFactoryName = "gsonConverterFactory"
        val gsonConverterFactory = PropertySpec.builder(gsonConverterFactoryName, gsonConverterClassName)
            .initializer("%T.create()", gsonConverterClassName)
            .build()
            .also{properties.add(it)}

        val coroutineCallAdapterFactoryName = "coroutineCallAdapterFactory"
        val coroutineCallAdapterFactory = PropertySpec.builder(coroutineCallAdapterFactoryName, coroutineCallAdapterClassName)
        .initializer("%T()", coroutineCallAdapterClassName)
            .build()
            .also{properties.add(it)}

        classBuilder.companionObject(TypeSpec.companionObjectBuilder("")
            .addProperty(baseURL)
            .build()
        )

        classBuilder.addProperty(okHttpClient)

        classBuilder.addProperty(gsonConverterFactory)

        classBuilder.addProperty(coroutineCallAdapterFactory)

        for (property in properties){
            nameAllocator.newName(property.name, property)
        }

        val retrofitCode = CodeBlock.builder()
        retrofitCode.add("%T.Builder()", retrofitClassName)
        retrofitCode.add(".baseUrl(%N)", baseURL)
        retrofitCode.add(".client(%N)", okHttpClient)
        retrofitCode.add(".addConverterFactory(%N)", gsonConverterFactory)
        retrofitCode.add(".addCallAdapterFactory(%N)", coroutineCallAdapterFactory)
        retrofitCode.add(".build()")

        val retrofit = PropertySpec.builder("retrofit", retrofitClassName)
            .initializer(retrofitCode.build())
            .build()

        classBuilder.addProperty(retrofit)

        val serviceCode = CodeBlock.builder()
        serviceCode.add("%N.create", retrofit)
        serviceCode.add("(%T::class.java)", generatedMockServiceClassName)

        classBuilder.addProperty(PropertySpec.builder("trackService", generatedMockServiceClassName)
            .initializer(serviceCode.build())
            .build()
        )

        val file = fileBuilder.addType(classBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        kaptKotlinGeneratedDir?.let{file.writeTo(File(it))}
    }
}
