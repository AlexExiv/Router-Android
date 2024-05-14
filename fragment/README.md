# Router-Android Fragment support

## Getting started

### Setting up the dependency

Gradle config for the core modules:

To project module add support of JitPack

```groovy

allprojects {
    repositories {
        ....
        maven { url 'https://jitpack.io' }
        ....
    }
}
```

In App module

```groovy
implementation "com.github.AlexExiv.Router-Android:router:$version"
implementation "com.github.AlexExiv.Router-Android:annotations:$version"
implementation "com.github.AlexExiv.Router-Android:fragment:$version" // add support of fragments

kapt "com.github.AlexExiv.Router-Android:processor:$version"
```

### Code example

You can find an example project at [sample-fragment](../sample-fragment)

#### RouteController
In the case of Fragment we have the same way of implementing of RouteControllers and Paths. Look at simple example bellow

```kotlin
class SimplePath: RoutePath

@Route
abstract class SimpleRouteController: RouteController<SimplePath, SimpleFragment>()
```

It generates the `onCreateView` method by default if you don't need to pass there extra parameters

Here, we have another example of a simple `RouteController`. In this case, we aim to pass parameters to the screen.
When you need to transmit data to your Fragment view, you'll need to implement `onCreateView` yourself and deliver the data to the view using the arguments property.

**Important:** Ensure that the data is serializable to be preserved in the state.

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

#### SimpleFragment

```kotlin
class SimpleFragment: Fragment(R.layout.fragment_simple), ViewFragment
{
    override var viewKey: String
        get() = _viewKey
        set(value)
        {
            _viewKey = value
        }

    override lateinit var router: Router // will be injected by framework
    override lateinit var localRouter: RouterLocal // will be injected by framework
    override lateinit var resultProvider: RouterResultProvider // will be injected by framework
    
    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        arguments // access data
    }
}
```

To avoid boilerplate code, we can define a base class for all Fragment view classes.

```kotlin

abstract class BaseFragment(@LayoutRes val layoutId: Int): Fragment(layoutId), ViewFragment
{
    override var viewKey: String
        get() = _viewKey
        set(value)
        {
            _viewKey = value
        }

    override lateinit var router: Router
    override lateinit var localRouter: RouterLocal
    override lateinit var resultProvider: RouterResultProvider
}

```

then our SimpleFragment becomes

```kotlin
class SimpleFragment: BaseFragment(R.layout.fragment_simple)
{
    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        arguments // access data
    }
}
```

See examples here
[BaseFragment](../sample-fragment/src/main/java/com/speakerboxlite/router/sample/base/BaseFragment.kt)
[SimplePath](../sample-fragment/src/main/java/com/speakerboxlite/router/sample/simple/SimplePath.kt)
[SimpleFragment](../sample-fragment/src/main/java/com/speakerboxlite/router/sample/simple/SimpleFragment.kt)

### Code example with ViewModel

#### RouteController

When working with ViewModels, it's beneficial to create a typealias of RouteController

```kotlin
typealias RouteControllerApp<Path, VM, V> = RouteControllerVM<Path, VM, AndroidViewModelProvider, V> // if you don't use Component fo injection
typealias RouteControllerApp<Path, VM, V> = RouteControllerVMC<Path, VM, AndroidViewModelProvider, V, AppComponent> // otherwise
```

If you're not passing arguments to your screen, the definition of RouteController remains the same.

```kotlin
class SimplePath: RoutePath

// Simple case
@Route
abstract class SimpleRouteController: RouteControllerApp<SimplePath, SimpleViewModel, SimpleFragment>()
```

If you wish to pass arguments, you must override the `onCreateViewModel` method and create the ViewModel as shown in the code below:

```kotlin
class SimplePath(val step: Int): RoutePath

// When you need to pass data to the ViewModel you have to override the onCreateViewModel method
@Route
abstract class SimpleRouteController: RouteControllerApp<SimplePath, SimpleViewModel, SimpleFragment>()
{
    override fun onCreateViewModel(modelProvider: AndroidViewModelProvider, path: SimplePath): SimpleViewModel =
        modelProvider.getViewModel { SimpleViewModel(path.step, it) }
}
```

#### SimpleFragment

Define a base fragment class with ViewModel from the `BaseFragment` class

```kotlin
abstract class BaseViewModelFragment<VM: BaseViewModel>(@LayoutRes layoutId: Int): BaseFragment(layoutId), ViewFragmentVM<VM>
{
    override lateinit var viewModel: VM // will be injected by framework
}
```

SimpleFragment class

```kotlin
class SimpleFragment: BaseViewModelFragment<SimpleViewModel>(R.layout.fragment_simple)
{
    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel // access ViewModel
    }
}
```

ViewModel class

```kotlin
class SimpleViewModel(val step: Int, app: Application): AndroidViewModel(app), ViewModel
{
    override lateinit var router: Router // will be injected by framework
    override lateinit var resultProvider: RouterResultProvider // will be injected by framework

    override var isInit: Boolean = false

    override fun onInit() //called after onInject method of the RouteController
    {

    }
}
```

See example of [BaseViewModel](../sample-fragment/src/main/java/com/speakerboxlite/router/sample/base/BaseViewModel.kt)
