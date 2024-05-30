package com.speakerboxlite.router

import java.io.Serializable

interface RoutePath: Serializable

interface RoutePathResult<Result>: RoutePath
