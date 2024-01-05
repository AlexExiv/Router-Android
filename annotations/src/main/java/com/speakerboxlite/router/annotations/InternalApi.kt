package com.speakerboxlite.router.annotations

@Target(allowedTargets = [
        AnnotationTarget.CLASS,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.TYPEALIAS,
        AnnotationTarget.CONSTRUCTOR])
@RequiresOptIn(level = RequiresOptIn.Level.ERROR,
    message = "This is an internal API. You can't use it")
annotation class InternalApi()
