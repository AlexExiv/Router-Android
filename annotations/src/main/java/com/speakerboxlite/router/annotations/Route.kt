package com.speakerboxlite.router.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Route(
    /**
     * Relative `URL` path to the controller.
     */
    val uri: String = "",

    /**
     * The default presentation style used in the absence of a specified style in the `route` method of the `Router`.
     */
    val presentation: Presentation = Presentation.Push,

    /**
     * Indicates that only one view with this path can exist in the views' hierarchy.
     * If a view with this path is already in the hierarchy, the `Router` will close all views until it reaches this view.
     */
    val singleTop: SingleTop = SingleTop.None,

    /**
     * The class file of the animation inherited from `AnimationController`
     */
    val animation: KClass<*> = Nothing::class)

/**
 * Generates a route controller for a Fragment by copying `Path` properties into Fragment arguments.
 *
 * Use this for simple Fragment migration cases where constructor properties of the `path` can be
 * mapped directly to Bundle values.
 */
@Repeatable
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class RouterFragmentRoute(
    /**
     * The `RoutePath` class that should open this Fragment.
     *
     * The path primary constructor properties are copied to Fragment arguments using the same
     * property names as Bundle keys.
     */
    val path: KClass<*>,

    /**
     * @see Route.uri
     */
    val uri: String = "",

    /**
     * @see Route.presentation
     */
    val presentation: Presentation = Presentation.Push,

    /**
     * @see Route.singleTop
     */
    val singleTop: SingleTop = SingleTop.None,

    /**
     * @see Route.animation
     */
    val animation: KClass<*> = Nothing::class,

    /**
     * The `ViewModel` class used by the generated controller.
     *
     * If set, the Fragment must implement `ViewFragmentVM`.
     */
    val viewModel: KClass<*> = Nothing::class,

    /**
     * The model provider class used to create the `viewModel`.
     *
     * If omitted with `viewModel` set, the Fragment router model provider is used.
     */
    val modelProvider: KClass<*> = Nothing::class,

    /**
     * The DI component class used by the generated component controller.
     */
    val component: KClass<*> = Nothing::class)

/**
 * Generates a route controller for a Fragment using a custom Fragment factory method.
 *
 * Use this when a Fragment cannot be created by directly copying `Path` properties into arguments.
 * The factory method must accept the `path` type and return the annotated Fragment type.
 */
@Repeatable
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class RouterFragment(
    /**
     * The `RoutePath` class that should open this Fragment.
     */
    val path: KClass<*>,

    /**
     * The Fragment factory method name.
     *
     * If empty, the value from `RouterApp.viewFactoryName` is used.
     */
    val factory: String = "",

    /**
     * @see Route.uri
     */
    val uri: String = "",

    /**
     * @see Route.presentation
     */
    val presentation: Presentation = Presentation.Push,

    /**
     * @see Route.singleTop
     */
    val singleTop: SingleTop = SingleTop.None,

    /**
     * @see Route.animation
     */
    val animation: KClass<*> = Nothing::class,

    /**
     * The `ViewModel` class used by the generated controller.
     *
     * If set, the Fragment must implement `ViewFragmentVM`.
     */
    val viewModel: KClass<*> = Nothing::class,

    /**
     * The model provider class used to create the `viewModel`.
     *
     * If omitted with `viewModel` set, the Fragment router model provider is used.
     */
    val modelProvider: KClass<*> = Nothing::class,

    /**
     * The DI component class used by the generated component controller.
     */
    val component: KClass<*> = Nothing::class)
