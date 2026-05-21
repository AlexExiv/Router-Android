package com.speakerboxlite.processor.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.RouteType
import com.speakerboxlite.router.annotations.SingleTop
import com.speakerboxlite.router.annotations.TabUnique
import com.squareup.kotlinpoet.ClassName

internal data class RouterAppInfo(
    val packageName: String,
    val sourceFile: KSFile?)

internal data class RouteClass(
    val className: ClassName,
    val pathName: ClassName,
    val viewName: ClassName,
    val componentCntrl: Boolean,
    val componentName: ClassName?,
    val isCompose: Boolean,
    val routeType: RouteType,
    val sourceFile: KSFile?,
    val uri: String,
    val presentation: Presentation,
    val singleTop: SingleTop,
    val tabsProperties: TabsProperties?,
    val animationClass: ClassName?,
    val chainPaths: List<ClassName>,
    val creatingInjector: Boolean,
    val middlewares: List<MiddlewareController>)

internal data class TabsProperties(
    val tabRouteInParent: Boolean,
    val backToFirst: Boolean,
    val tabUnique: TabUnique)

internal data class MiddlewareController(
    val annotation: KSClassDeclaration,
    val typeElement: KSClassDeclaration,
    val className: ClassName,
    val varName: String,
    val order: Int,
    val hasComponent: Boolean,
    val sourceFile: KSFile?)
