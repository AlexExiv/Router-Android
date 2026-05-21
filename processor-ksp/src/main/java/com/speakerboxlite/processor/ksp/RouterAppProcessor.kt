package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.speakerboxlite.processor.ksp.ext.annotationValue

internal class RouterAppProcessor(private val reporter: KspErrorReporter)
{
    fun findRouterApp(resolver: Resolver): RouterAppInfo?
    {
        val appSymbols = resolver.getSymbolsWithAnnotation(ROUTER_APP_ANNOTATION)
            .mapNotNull {
                it as? KSClassDeclaration ?: run {
                    reporter.fail("Router KSP: @RouterApp can be used only on classes.", it)
                    null
                }
            }
            .toList()

        return when (appSymbols.size)
        {
            0 ->
            {
                reporter.fail("Router KSP: RouterComponentImpl package is explicit in KSP. Add @RouterApp to your Application class.")
                null
            }
            1 -> RouterAppInfo(
                packageName = appSymbols[0].packageName.asString(),
                viewFactoryName = appSymbols[0].annotationValue(ROUTER_APP_ANNOTATION, "viewFactoryName") ?: "newInstance",
                sourceFile = appSymbols[0].containingFile)
            else ->
            {
                appSymbols.forEach { reporter.fail("Router KSP: only one @RouterApp class is allowed in a module.", it) }
                null
            }
        }
    }
}
