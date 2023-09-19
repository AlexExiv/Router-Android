package com.speakerboxlite.processor

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement

abstract class RouteControllerProcessorBase(val processingEnv: ProcessingEnvironment,
                                            val kaptKotlinGeneratedDir: String,
                                            val mainRouterPack: String): RouteControllerProcessorInterface
{
    override fun checkElement(element: TypeElement): Boolean
    {
        val thisName = this::class.simpleName!!.replace("Processor", "")
        return element.hasParent(thisName)
    }
}