package com.speakerboxlite.processor

import com.speakerboxlite.router.annotations.RouteType
import com.squareup.kotlinpoet.ClassName

data class RouteClass(val className: ClassName,
                      val pathName: ClassName,
                      val viewName: ClassName,
                      val componentCntrl: Boolean,
                      val componentName: ClassName?,
                      val isCompose: Boolean,
                      val routeType: RouteType)
