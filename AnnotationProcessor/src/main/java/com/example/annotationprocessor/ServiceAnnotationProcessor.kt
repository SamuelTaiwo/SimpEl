package com.example.annotationprocessor

import com.example.annotations.ServiceGenerator
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
class ServiceAnnotationProcessor : AbstractProcessor(){

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        val stringClassName = String::class.asClassName()

        val trackSearchName = ClassName("com.example.simpel.tracks","TrackSearch")
        val individualTrackName = ClassName("com.example.simpel.track","IndividualTrack")

        val queryAnnotationName = ClassName("retrofit2.http","Query")
        val getAnnotationName = ClassName("retrofit2.http","GET")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ServiceGenerator::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        roundEnv.getElementsAnnotatedWith(ServiceGenerator::class.java)
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

    private fun processAnnotation(element: Element) {
        val className = element.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(element).toString()

        val fileName = "Generated$className"
        val fileBuilder = FileSpec.builder(pack, fileName)
        val interfaceBuilder = TypeSpec.interfaceBuilder(fileName)

        interfaceBuilder.addFunction(
            FunSpec.builder("fetchTracksAsync")
                .addAnnotation(AnnotationSpec.builder(getAnnotationName)
                    .addMember("%S","2.0/")
                    .build()
                )
                .addParameter(ParameterSpec.builder("track", stringClassName)
                    .addAnnotation(createQuery("track"))
                    .build()
                )
                .addParameter(ParameterSpec.builder("method", stringClassName)
                    .addAnnotation(createQuery("method"))
                    .defaultValue("%S","track.search")
                    .build()
                )
                .addParameter(ParameterSpec.builder("apiKey", stringClassName)
                    .addAnnotation(createQuery("api_key"))
                    .defaultValue("%S","98ceff85f274d0607f90af8a2755cd5b")
                    .build()
                )
                .addParameter(ParameterSpec.builder("format", stringClassName)
                    .addAnnotation(createQuery("format"))
                    .defaultValue("%S","json")
                    .build()
                )
                .addModifiers(KModifier.ABSTRACT)
                .addModifiers(KModifier.SUSPEND)
                .returns(trackSearchName)
                .build()
        )

        interfaceBuilder.addFunction(
            FunSpec.builder("fetchTrackAsync")
                .addAnnotation(AnnotationSpec.builder(getAnnotationName)
                    .addMember("%S","2.0/")
                    .build()
                )
                .addParameter(ParameterSpec.builder("track", stringClassName)
                    .addAnnotation(createQuery("track"))
                    .build()
                )
                .addParameter(ParameterSpec.builder("artist", stringClassName)
                    .addAnnotation(createQuery("artist"))
                    .build()
                )
                .addParameter(ParameterSpec.builder("method", stringClassName)
                    .addAnnotation(createQuery("method"))
                    .defaultValue("%S","track.search")
                    .build()
                )
                .addParameter(ParameterSpec.builder("apiKey", stringClassName)
                    .addAnnotation(createQuery("api_key"))
                    .defaultValue("%S","98ceff85f274d0607f90af8a2755cd5b")
                    .build()
                )
                .addParameter(ParameterSpec.builder("format", stringClassName)
                    .addAnnotation(createQuery("format"))
                    .defaultValue("%S","json")
                    .build()
                )
                .addModifiers(KModifier.ABSTRACT)
                .addModifiers(KModifier.SUSPEND)
                .returns(individualTrackName)
                .build()
        )

        val file = fileBuilder.addType(interfaceBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        kaptKotlinGeneratedDir?.let{file.writeTo(File(it))}
    }
}