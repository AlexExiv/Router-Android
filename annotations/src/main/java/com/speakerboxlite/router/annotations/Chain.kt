package com.speakerboxlite.router.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Chain(val closeItems: Array<KClass<*>>)
