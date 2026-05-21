package com.speakerboxlite.processor.ksp.ext

import com.squareup.kotlinpoet.ClassName

internal fun ClassName.isEmptyAnimationMarker(): Boolean =
    simpleName == "Nothing" || canonicalName == "java.lang.Void"
