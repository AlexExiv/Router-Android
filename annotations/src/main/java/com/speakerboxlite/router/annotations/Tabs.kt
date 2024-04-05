package com.speakerboxlite.router.annotations

enum class TabUnique
{
    None, Class, Equal
}

/**
 * Mark route controllers that contain tabs by this annotation to avoid showing of other screens with tabs
 * in a tab of this tabs screen. In other words It shows a new screen with tabs on the full screen.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Tabs(
    /**
     * If true then any route operation will be executed in the parent router
     * */
    val tabRouteInParent: Boolean = false,
    val backToFirst: Boolean = true,
    val tabUnique: TabUnique = TabUnique.Class)

/*
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Tab
*/