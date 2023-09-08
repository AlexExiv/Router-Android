package com.speakerboxlite.router.exceptions

import com.speakerboxlite.router.RoutePath

class RouteNotFoundException(path: RoutePath): RuntimeException("Couldn't find a route: ${path::class.simpleName}")
