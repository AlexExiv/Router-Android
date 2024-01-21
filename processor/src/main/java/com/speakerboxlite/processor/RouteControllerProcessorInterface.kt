package com.speakerboxlite.processor

import javax.lang.model.element.TypeElement

interface RouteControllerProcessorInterface
{
    fun checkElement(element: TypeElement): Boolean
    fun createClass(element: TypeElement): RouteClass
}