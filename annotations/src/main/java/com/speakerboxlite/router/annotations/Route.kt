package com.speakerboxlite.router.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Route(val uri: String = "",
                       val presentation: Presentation = Presentation.Push,
                       val singleton: Boolean = false,
                       val animation: KClass<*> = Nothing::class)
