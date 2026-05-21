package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration

internal class RouterAppProcessor(private val logger: KSPLogger)
{
    fun findRouterApp(resolver: Resolver): RouterAppInfo?
    {
        val appSymbols = resolver.getSymbolsWithAnnotation(ROUTER_APP_ANNOTATION)
            .mapNotNull {
                it as? KSClassDeclaration ?: run {
                    logger.error("Router KSP: @RouterApp can be used only on classes.", it)
                    null
                }
            }
            .toList()

        return when (appSymbols.size)
        {
            0 ->
            {
                logger.error("Router KSP: RouterComponentImpl package is explicit in KSP. Add @RouterApp to your Application class.")
                null
            }
            1 -> RouterAppInfo(appSymbols[0].packageName.asString(), appSymbols[0].containingFile)
            else ->
            {
                appSymbols.forEach { logger.error("Router KSP: only one @RouterApp class is allowed in a module.", it) }
                null
            }
        }
    }
}
