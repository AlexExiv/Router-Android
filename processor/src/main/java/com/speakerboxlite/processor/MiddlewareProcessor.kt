package com.speakerboxlite.processor

import com.speakerboxlite.router.annotations.GlobalMiddleware
import com.speakerboxlite.router.annotations.Middleware
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

data class MiddlewareController(val annotation: TypeElement,
                                val typeElement: TypeElement,
                                val className: ClassName,
                                val varName: String,
                                val order: Int)

class MiddlewareProcessor(val roundEnv: RoundEnvironment)
{
    private var middlewares = listOf<MiddlewareController>()
    private var middlewaresGlobal = listOf<MiddlewareController>()

    var hadComponent: Boolean = false
        private set

    fun prepareMiddlewares(): List<MiddlewareController>
    {
        val middlewareAnnots = roundEnv.getElementsAnnotatedWith(Middleware::class.java)
        val middlewaresCntrls = mutableListOf<MiddlewareController>()//implementations of middleware controllers

        for (m in middlewareAnnots)
        {
            val annotation = m as TypeElement
            val mcs = roundEnv.getElementsAnnotatedWith(annotation)
                .mapNotNull { it as? TypeElement }
                .filter { it.hasAnyParent(MIDDLEWARE_CLASSES, true) }

            when (mcs.size)
            {
                0 -> {}
                1 -> middlewaresCntrls.add(MiddlewareController(annotation, mcs[0], mcs[0].asClassName(), "mid_${mcs[0].simpleName.toString().lowercase()}", middlewaresCntrls.size))
                else -> {}
            }
        }

        this.middlewares = middlewaresCntrls.toList()

        val globalMiddlewaresCntrls = roundEnv.getElementsAnnotatedWith(GlobalMiddleware::class.java)
        middlewaresCntrls.clear()

        for (m in globalMiddlewaresCntrls)
        {
            val cntrl = m as TypeElement
            if (!cntrl.hasAnyParent(MIDDLEWARE_CLASSES, true))
                continue

            val annot = m.getAnnotation(GlobalMiddleware::class.java)
            middlewaresCntrls.add(MiddlewareController(cntrl, cntrl, cntrl.asClassName(), "mid_${cntrl.simpleName.toString().lowercase()}", annot.order))
        }

        this.middlewaresGlobal = middlewaresCntrls.sortedBy { it.order }.toList()
        val total = this.middlewares + this.middlewaresGlobal
        hadComponent = total.firstOrNull { it.typeElement.hasParent(MIDDLEWARE_COMPONENT_CLASS, true) } != null

        return total
    }

    fun buildMiddlewares(typeElement: TypeElement): List<MiddlewareController>
    {
        val middlewares = typeElement.collectAnnotations()
            .mapNotNull { a -> middlewares.firstOrNull { it.annotation == a } }

        return middlewares.reversed() + middlewaresGlobal
    }

    companion object
    {
        val MIDDLEWARE_COMPONENT_CLASS = "MiddlewareControllerComponent"
        val MIDDLEWARE_CLASSES = listOf("MiddlewareController", MIDDLEWARE_COMPONENT_CLASS)
    }
}