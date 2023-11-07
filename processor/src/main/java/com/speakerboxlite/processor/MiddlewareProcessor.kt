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

    fun prepareMiddlewares(): List<MiddlewareController>
    {
        val middlewares = roundEnv.getElementsAnnotatedWith(Middleware::class.java)
        val middlewaresCntrls = mutableListOf<MiddlewareController>()//implementations of middleware controllers

        for (m in middlewares)
        {
            val at = m as TypeElement
            val mcs = roundEnv.getElementsAnnotatedWith(at)
                .mapNotNull { it as? TypeElement }
                .filter { it.hasAnyParent(AnnotationProcessor.MIDDLEWARE_CLASSES, true) }

            when (mcs.size)
            {
                0 -> {}
                1 -> middlewaresCntrls.add(MiddlewareController(at, mcs[0], mcs[0].asClassName(), "mid_${mcs[0].simpleName.toString().lowercase()}", middlewaresCntrls.size))
                else -> {}
            }
        }

        this.middlewares = middlewaresCntrls.toList()

        val globalMiddlewares = roundEnv.getElementsAnnotatedWith(GlobalMiddleware::class.java)
        middlewaresCntrls.clear()

        for (m in globalMiddlewares)
        {
            val at = m as TypeElement
            if (!at.hasAnyParent(AnnotationProcessor.MIDDLEWARE_CLASSES, true))
                continue

            val a = m.getAnnotation(GlobalMiddleware::class.java)
            middlewaresCntrls.add(MiddlewareController(at, at, at.asClassName(), "mid_${at.simpleName.toString().lowercase()}", a.order))
        }

        this.middlewaresGlobal = middlewaresCntrls.sortedBy { it.order }.toList()

        return this.middlewares + this.middlewaresGlobal
    }

    fun buildMiddlewares(typeElement: TypeElement): List<MiddlewareController>
    {
        val middlewares = typeElement.collectAnnotations()
            .mapNotNull { a -> middlewares.firstOrNull { it.annotation == a } }

        return middlewares.reversed() + middlewaresGlobal
    }
}