package com.speakerboxlite.processor

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.TypeElement

class RouteControllerProcessorManager
{
    val processors = mutableListOf<RouteControllerProcessorInterface>()
    var hadComponent: Boolean = false
        private set

    fun createClass(element: TypeElement): ClassName
    {
        val processor = processors.firstOrNull { it.checkElement(element) } ?: throw RuntimeException("Couldn't find")
        if (processor is RouteControllerVMCProcessor)
            hadComponent = true
        return processor.createClass(element)
    }

    fun register(processor: RouteControllerProcessorInterface)
    {
        processors.add(processor)
    }
}