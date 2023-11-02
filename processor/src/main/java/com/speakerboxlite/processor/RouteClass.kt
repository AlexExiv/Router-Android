package com.speakerboxlite.processor

import com.squareup.kotlinpoet.ClassName

data class RouteClass(val className: ClassName,
                      val pathName: ClassName,
                      val viewName: ClassName,
                      val componentName: ClassName?)
