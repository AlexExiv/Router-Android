# Router-Android

[![](https://jitpack.io/v/AlexExiv/Router-Android.svg)](https://jitpack.io/#AlexExiv/Router-Android)

Provides an easy solution for screen navigation, eliminating the need to understand the Fragment lifecycle or implement transitions in Compose. Supports mixed transitions between Fragments and Compose screens.

The simple example of creating a Route for navigation. Here, ScreenView is a pseudo abstract class. For actual implementations, please refer to:
[sample-fragment](sample-fragment) : Example project with fragments
[sample-compose](sample-compose) : Example project with Compose
[sample-mixed](sample-mixed) : Example mixed project containing fragments and Compose

```kotlin
// Pseudo view
class ScreenView: View
{
    override var viewKey: String = UUID.randomUUID().toString() // unique id for this view
}

// Define path to the screen
class ScreenPath: RouterPath

// Connect the path and screen using RouteController
// ScreenView can be Compose view or Fragment
@Route
abstract class ScreenRouteController: RouteController<ScreenPath, ScreenView>()

// Then somewhere in the code (View or ViewModel)
router.route(ScreenPath())

```

## Modules

Gradle config for the core modules:
```gradle
implementation "com.github.AlexExiv.Router-Android:router:$version"
implementation "com.github.AlexExiv.Router-Android:annotations:$version"
kapt "com.github.AlexExiv.Router-Android:processor:$version"
```

The framework has additional modules:
1. **fragment** - module for android projects which use only fragment and activities. Additional modules to the config:
```gradle
implementation "com.github.AlexExiv.Router-Android:fragment:$version"
```
[compose/README.md](compose/README.md) \ Information about using compose with the Router.

2. **compose** - module for projects which use only compose for UI. Additional modules to the config:
```gradle
implementation "com.github.AlexExiv.Router-Android:compose:$version"
```
[fragment/README.md](fragment/README.md) \ Information about using fragments with the Router.
3. **fragmentcompose** - module for projects which use both fragment and compose for UI. Additional modules to the config:
```gradle
implementation "com.github.AlexExiv.Router-Android:fragment:$version"
implementation "com.github.AlexExiv.Router-Android:compose:$version"
implementation "com.github.AlexExiv.Router-Android:fragmentcompose:$version"
```

Please, look at samples for each module to understand how to use the router in various scenarios.

## Key parts of the framework:

1. RoutePath - path to a screen (one screen can have several paths)
2. RouteController - a controller that connects a path with a screen and implements routing logic. It creates a View, ViewModel, connects them, creates components for injections, and injects dependencies.
3. Router - conducts routing between screens
4. Chain - sequence of screens
5. Result Api - convenient API to get result from called screen
6. Middleware - interceptor of navigations; it can prevent navigation, or replace it with another route, or perform some operations.

## RoutePath

A class that defines a path to the screen. The properties of this class are parameters that you want 
to send to the screen. Use a simple class when the path doesn’t provide any parameters to the screen. 
Use a data class when you want to pass one or more parameters. Avoid passing heavy data like images or 
long arrays through it. It is usually used to pass small portions of data such as IDs or filter parameters.

### Examples
```kotlin
class MainPath: RoutePath //Simple path

data class StepPath(val step: Int): RoutePath //Path with parameters

data class DialogPath(val title: String = "",
                      val message: String = "",
                      val okBtn: String = "",
                      val cancelBtn: String = ""): RoutePathResult<Boolean>  //Path with parameters that returns Boolean value
```

## RouteController

A class marked by the annotation @Route and implementing one of the children of the RouteControllerInterface.
It connects a path with a screen and assembles the screen. Currently supports 4 types of routes:
1. Simple with only Fragment or Compose as view `RouteController`
2. With only Fragment or Compose as View and Dagger injection `RouteControllerC`
3. Fragment or Compose as View and ViewModel `RouteControllerVM`
4. Fragment or Compose as View and ViewModel and Dagger injection `RouteControllerVMC`

By default this class should be abstract because all its method generated by the framework. You can
override its if you need to do some extra action, for example add something to the Bundle of the Fragment.

### View is fragment

Here is an example of a simple `RouteController`. The `onCreateView` method is generated by the framework.

```kotlin
class SimplePath: RoutePath

@Route
abstract class SimpleRouteController: RouteController<SimplePath, SimpleFragment>()
```

Here is another example of a simple `RouteController`. In this case, we implement the `onCreateView` method to pass the `title` parameter to the Fragment.
When you need to pass data to your fragment, you have to implement `onCreateView` yourself and place data in the arguments bundle.

```kotlin
data class SimplePath(val title: String): RoutePath

@Route
class SimpleRouteController: RouteController<SimplePath, SimpleFragment>()
{
    override fun onCreateView(path: SimplePath): SimpleFragment =
        SimpleFragment().apply {
            arguments = Bundle().apply {
                putString("TITLE_KEY", path.title)
            }
        }
}
```

### Dagger injection

In case you use dagger injection you have to replace `RouteController` by `RouteControllerC`
Example of RouteControllerC with dependency injection where AppComponent is a Component interface

```kotlin
class SimpleComponentPath: RoutePath

@Route
abstract class SimpleComponentRouteController: RouteControllerC<SimpleComponentPath, SimpleComponentFragment, AppComponent>()
```

To simplify you can make an alise for RouteControllerC

```kotlin
typealias RouteControllerApp<Path, V> = RouteControllerC<Path, V, AppComponent>
```

and then use it in your code:

```kotlin
@Route
abstract class SimpleComponentRouteController: RouteControllerApp<SimpleComponentPath, SimpleComponentFragment>()
```

#### Inject to RouteController

If you need to inject dependencies from your AppComponent, you can override the `fun onInject(component: Any)` method.

```kotlin
override fun onInject(component: Any)
{
    (component as AppComponent).inject(this)
}
```

#### Create sub component

Sometimes you need to create subcomponent to connect or pass data between several screens. 
At the first you have to redefine `RouteControllerC` and now pass your `SubComponent` as the parameter

```kotlin
typealias RouteControllerSubComponent<Path, V> = RouteControllerC<Path, V, SubComponent>
```

Then in the root screen (owner of the sub component) in its RouteController override the `fun onCreateInjector(path: Path, component: Any): Any` method

```kotlin
override fun onCreateInjector(path: SharedPath, component: Any): Any =
    DaggerSharedComponent.builder()
        .appComponent(component as AppComponent)
        .sharedModule(SharedModule(SharedData()))
        .build()
```

Inherit RouteControllers from `RouteControllerSubComponent` in the other paths to inject dependencies from this component

### ViewModel

In case you use MVVM pattern you have to replace `RouteController` by `RouteControllerVM` or if you use dagger injection by `RouteControllerVMC`

Example of using for the Fragment:

```kotlin
typealias RouteControllerApp<Path, VM, V> = RouteControllerVMC<Path, VM, AndroidViewModelProvider, V, AppComponent>

class TabsPath: RoutePath

// Simple case
@Route
abstract class TabsRouteController: RouteControllerApp<TabsPath, TabsViewModel, TabsFragment>()

class StepPath(val step: Int): RoutePath

// When you need to pass data to the ViewModel you have to override the onCreateViewModel method
@Route
abstract class StepRouteController: RouteControllerApp<StepPath, StepViewModel, StepFragment>()
{
    override fun onCreateViewModel(modelProvider: AndroidViewModelProvider, path: StepPath): StepViewModel =
        modelProvider.getViewModel { StepViewModel(path.step, it) }
}
```

## Router

Router is injected to Views and ViewModels by the framework.

```kotlin
// Call the method to navigate to another screen
router.route(SreenPath())

// Call the method to navigate to another screen and get result from it
router.routeWithResult(this, ScreenPath()) { result -> }
```

## Chain

A chain is a sequence of screens connected by specific logic. It can represent a wizard with steps like step0, step1, step2, and so on. When the `close` method is invoked by any of the entries, the entire chain closes.

You can define a chain of screens in the RouteController using the `@Chain` annotation. It has one parameter that specifies the paths which close the chain. 

It's important to note that calling the `back` method doesn't close a chain.

```kotlin
data class ChainPath(val step: Int): RoutePathResult<Int>

@Chain([ChainStepPath::class])
@Route
abstract class ChainRouteController: RouteControllerApp<ChainPath, ChainViewModel, ChainFragment>()
{
    override fun onCreateViewModel(modelProvider: AndroidViewModelProvider, path: ChainPath): ChainViewModel =
        modelProvider.getViewModel { ChainViewModel(path.step, it) }
}
```

If you want to explore a comprehensive example, you can refer to the `Sample-Fragment` module. It provides a detailed illustration of the concepts discussed, allowing you to see the implementation in action.

## Result Api

An easy and convenient way to obtain results from the called screen. To use it, the called path should implement the
`RoutePathResult<Result>` interface, where `Result` is the type of the returning value. Then, in code, simply call the
`routeWithResult` method. The first parameter is a reference to the ViewResult subclass, and the second is the path to which
you want to navigate. ViewResult subclasses are Fragments and ViewModels that implement the ViewResult interface.

```kotlin
router.routeWithResult(this, ScreenPath()) { 
    /* handle result here */
    
    it.vr // reference to the object passed to the method
    it.result // result
    it.vr.onHandleResult(it.result)
}
```

In the result closure, you receive a data structure that has a reference to the ViewResult subclass passed to the method
and the result of the called screen's job.

**Important:** why do we pass the ViewResult to the `routeWithResult` method? Why don't we use `this` context of
the ViewModel in the closure? It's because the Fragment or ViewModel could be recreated by the system
by the time you get the result in the closure. Therefore, the Result API returns to you a fresh reference to the Fragment
or ViewModel.

**Chains case:** Result from a screen that is part of a chain is delivered to the caller of the chain.

## Middleware

Middleware serves as a tool to intercept navigation between screens under specific conditions. 
For instance, when a user attempts to navigate to a screen that requires authentication, but the user is not signed in, the Middleware intercepts the navigation and redirects the user to the sign-in screen.

Example:

```kotlin
// Create middleware annotation
@Middleware
annotation class MiddlewareAuth

// Mark MiddlewareController as MiddlewareAuth
@MiddlewareAuth
class MiddlewareControllerAuth: MiddlewareControllerComponent
{
    @Inject
    lateinit var userData: UserData

    override fun onInject(component: Any)
    {
        (component as AppComponent).inject(this)
    }

    // This method will be called during navigation
    override fun onRoute(router: Router, prev: RoutePath?, next: RouteParamsGen): Boolean
    {
        if (!userData.isLogin.value!!)
        {
            router.route(AuthPath(next))
            return true
        }

        return false
    }
}

// Then mark the RouteController as MiddlewareAuth 
@Route
@MiddlewareAuth
abstract class TabAuthRouteController: RouteControllerApp<TabPath2, TabViewModel, TabFragment>()
```

If you want to explore a comprehensive example, you can refer to the `Sample-Fragment` module. It provides a detailed illustration of the concepts discussed, allowing you to see the implementation in action.

