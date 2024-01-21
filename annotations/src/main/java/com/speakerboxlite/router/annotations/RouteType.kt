package com.speakerboxlite.router.annotations

enum class RouteType
{
    Simple, Dialog, BTS;

    val isNoStackStructure: Boolean get() = this == Dialog || this == BTS
}