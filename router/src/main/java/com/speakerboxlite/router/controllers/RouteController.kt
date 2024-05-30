package com.speakerboxlite.router.controllers

import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.annotations.RouteType
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.View
import com.speakerboxlite.router.pattern.UrlMatcher
import com.speakerboxlite.router.annotations.Presentation
import com.speakerboxlite.router.annotations.SingleTop
import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Implement this controller if you are using a simple view without a ViewModel and Component for injection
 */
abstract class RouteController<Path: RoutePath, V: View>: RouteControllerInterface<Path, V>
{
    lateinit var pathClass: KClass<Path>
    var pattern: UrlMatcher? = null
    var preferredAnimationController: AnimationController? = null
    var animationControllerFactory: AnimationControllerFactory? = null

    final override var singleTop: SingleTop = SingleTop.None
    final override var creatingInjector: Boolean = false
    final override var preferredPresentation: Presentation = Presentation.Push

    var chainPaths: List<KClass<*>> = listOf()
    final override val isChain: Boolean get() = chainPaths.isNotEmpty()

    final override var isCompose: Boolean = false
    final override var routeType: RouteType = RouteType.Simple
    final override var tabProps: TabsProperties? = null

    final override var middlewares: List<MiddlewareController> = listOf()

    override val params: Serializable? get() = null

    override fun check(url: String): Boolean = pattern?.matches(url) ?: false

    override final fun check(path: RoutePath): Boolean = pathClass.isInstance(path)

    override final fun convert(url: String): RoutePath
    {
        val match = pattern!!.match(url)!!

        val query: Map<String, String>
        val comp = url.split("?")
        if (comp.size > 1)
        {
            query = mutableMapOf()
            val pairs = comp[1].split("&")
            pairs.forEach {
                val pair = it.split("=")
                if (pair.size > 1)
                    query[pair[0]] = pair[1]
                else
                    query[pair[0]] = ""
            }
        }
        else
        {
            query = mapOf()
        }

        return convert(match.parameters, query)
    }

    open fun convert(path: Map<String, String>, query: Map<String, String>): Path =
        pathClass.createInstance()

    override fun isPartOfChain(clazz: KClass<*>): Boolean = chainPaths.indexOfFirst { it == clazz } != -1

    override fun animationController(presentation: Presentation?, view: View): AnimationController? =
        preferredAnimationController ?: animationControllerFactory?.onCreate(presentation, view)

    abstract override fun onCreateView(path: Path): V

    override fun onBeforeRoute(router: Router, current: Path, next: RouteParamsGen): Boolean = false
    override fun onRoute(router: Router, prev: RoutePath?, current: RouteParams<Path>): Boolean = false
    override fun onClose(router: Router, current: Path, prev: RoutePath?): Boolean = false
}