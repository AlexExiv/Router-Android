package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal interface RouteControllerProcessorInterface
{
    fun checkElement(element: KSClassDeclaration): Boolean
    fun createClass(element: KSClassDeclaration): RouteClass?
}
