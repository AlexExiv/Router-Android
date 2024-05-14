# Router-Android Compose support

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
implementation "com.github.AlexExiv.Router-Android:compose:$version" // add support of compose

kapt "com.github.AlexExiv.Router-Android:processor:$version"
```

### Code example

You can find an example project at [sample-compose](../sample-compose)

#### RouteController
In the case of Compose we have the same way of implementing of RouteControllers and Paths. Look at simple example bellow

```kotlin
class SimplePath: RoutePath

@Route
abstract class SimpleRouteController: RouteController<SimplePath, SimpleView>()
```

It generates the `onCreateView` method by default if you don't need to pass there extra parameters

Here, we have another example of a simple `RouteController`. In this case, we aim to pass parameters to the screen.
When you need to transmit data to your Compose view, you'll need to implement `onCreateView` yourself and deliver the data to the view, perhaps in the constructor.

**Important:** Ensure that the data is serializable to be preserved in the state.

```kotlin
data class SimplePath(val title: String): RoutePath

@Route
class SimpleRouteController: RouteController<SimplePath, SimpleView>()
{
    override fun onCreateView(path: SimplePath): SimpleView = SimpleView(path.title)
}
```

#### SimpleView

```kotlin
class SimpleView: ViewCompose
{
    override var viewKey: String = UUID.randomUUID().toString() // unique id for this view

    @Composable
    override fun Root() // this is a Root view for the screen
    {
        Simple()
    }
}

@Composable
fun Simple()
{
    Box(modifier = Modifier.fillMaxSize()) {
        Row { 
            Column {
                Text(text = "I'm a simple Compose view")
            }
        }
    }
}
```

To avoid boilerplate code, we can define a base class for all Compose view classes.

```kotlin

abstract class BaseViewCompose: ViewCompose
{
    override var viewKey: String = UUID.randomUUID().toString()
}
```

then our SimpleView becomes

```kotlin

class SimpleView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Simple()
    }
}
```

### Code example with ViewModel

#### RouteController

When working with ViewModels, it's beneficial to create a typealias of RouteController

```kotlin
typealias RouteControllerApp<Path, VM, V> = RouteControllerVM<Path, VM, AndroidComposeViewModelProvider, V> // if you don't use Component fo injection
typealias RouteControllerApp<Path, VM, V> = RouteControllerVMC<Path, VM, AndroidComposeViewModelProvider, V, AppComponent> // otherwise
```

If you're not passing arguments to your screen, the definition of RouteController remains the same.

```kotlin
class SimplePath: RoutePath

// Simple case
@Route
abstract class SimpleRouteController: RouteControllerApp<SimplePath, SimpleViewModel, SimpleView>()
```

If you wish to pass arguments, you must override the `onCreateViewModel` method and create the ViewModel as shown in the code below:

```kotlin
class SimplePath(val step: Int): RoutePath

// When you need to pass data to the ViewModel you have to override the onCreateViewModel method
@Route
abstract class SimpleRouteController: RouteControllerApp<SimplePath, SimpleViewModel, SimpleView>()
{
    override fun onCreateViewModel(modelProvider: AndroidComposeViewModelProvider, path: SimplePath): SimpleViewModel =
        modelProvider.getViewModel { SimpleViewModel(path.step, it) }
}
```

If you use Hilt as dependency injection framework use [compose-hilt](../compose-hilt) module and read [documentation](../compose-hilt/README.md) to know how to pass arguments.

#### SimpleView

```kotlin
class SimpleView: BaseViewCompose()
{
    @Composable
    override fun Root()
    {
        Simple(viewModel = routerViewModel())
    }
}

@Composable
fun Simple(
    viewModel: SimpleViewModel // our ViewModel
)
{
    val router = LocalRouter.currentOrThrow // Access current router

    Surface(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize()){
            Row {
                Text(text = "I'm a SimpleView with ViewModel")
            }
        }
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
