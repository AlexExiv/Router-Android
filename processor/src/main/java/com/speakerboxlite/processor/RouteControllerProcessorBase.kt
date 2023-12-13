package com.speakerboxlite.processor

import com.speakerboxlite.router.annotations.RouteType
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

    protected fun isCompose(view: TypeElement): Boolean = view.hasParent("ViewCompose", true)

    protected fun getRouteType(view: TypeElement): RouteType
    {
        if (view.hasParent("ViewDialog", true))
            return RouteType.Dialog
        if (view.hasParent("ViewBTS", true))
            return RouteType.BTS

        return RouteType.Simple
    }
}