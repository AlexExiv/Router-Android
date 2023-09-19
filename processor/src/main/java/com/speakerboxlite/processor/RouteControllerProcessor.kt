package com.speakerboxlite.processor

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.TypeElement

interface RouteControllerProcessor
{
    fun checkElement(element: TypeElement): Boolean
    fun createClass(element: TypeElement): ClassName
}