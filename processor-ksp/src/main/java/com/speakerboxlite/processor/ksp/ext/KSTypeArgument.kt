package com.speakerboxlite.processor.ksp.ext

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter

internal fun List<KSTypeArgument>.mapClassDeclarations(typeAliasArguments: Map<String, KSClassDeclaration> = mapOf()): List<KSClassDeclaration> =
    mapNotNull {
        when (val declaration = it.type?.resolve()?.declaration)
        {
            is KSClassDeclaration -> declaration
            is KSTypeParameter -> typeAliasArguments[declaration.name.asString()]
            else -> null
        }
    }
