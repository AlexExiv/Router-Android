# Router-Android Compose support

## Getting started

### Setting up the dependencies

Gradle config for the core modules:

To project module add support of JitPack

```groovy

allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
        ...
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

#### Application

In the first step, we need to configure our `Application` class.
Your `Application` class should be inherited from the `ComposeApplication` class and create and initialize the `RouterComponentImpl` in the `onCreateRouter` method.

If you're not using Component injection, the App class should look like this:

```kotlin
class App: ComposeApplication<RouterComponentImpl>()
{
    override fun onCreateRouter()
    {
        routerComponent = RouterComponentImpl()
        routerComponent.initialize(MainPath(), { _, _ -> AnimationControllerComposeSlide() })
    }
}
```

Otherwise:

```kotlin
class App: ComposeApplication<RouterComponentImpl>()
{
    lateinit var component: AppComponent

    override fun onCreateComponent()
    {
        super.onCreateComponent()

        component = DaggerAppComponent.builder()
            .appModule(AppModule(AppData("App String")))
            .userModule(UserModule(UserData()))
            .build()
    }

    override fun onCreateRouter()
    {
        routerComponent = RouterComponentImpl()
        routerComponent.initialize(MainPath(), { _, _ -> AnimationControllerComposeSlide() }, component)
    }
}
```

#### MainActivity

The second step involves implementing a simple `MainActivity` class. This class should inherit from the `ComposeActivity` class
from the [bootstrap](src/main/java/com/speakerboxlite/router/compose/bootstrap/ComposeActivity.kt) package.

Simple `MainActivity` class:

```kotlin
class MainActivity: ComposeActivity()
{
    /**
     * Root content. It's entry point for your compose view
     */
    @Composable
    override fun Content()
    {
        RouterTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                ComposeNavigator(router = router)
            }
        }
    }
}
```

It's enough if you're using single activity way. Now let's create our first Compose view

#### SimpleView

```kotlin
/**
 * This view represents screen
 */
class SimpleView: BaseViewCompose
{
    @Composable
    override fun Root() // this is a Root view for the screen here you can place your Compose code
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

#### SimplePath

Using with class we will navigate to the `SimpleView` screen

```kotlin
class SimplePath: RoutePath
```

#### RouteController

Now we have to connect `SimplePath` with `SimpleView`.
In the case of Compose we have the same way of implementing of RouteControllers and Paths. Look at simple example bellow

```kotlin
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

### Code example with ViewModel

Now let's take a look at how `RouteController` looks like when our `ComposeView` has a `ViewModel`

#### RouteController

When working with ViewModels, it's beneficial to create a `typealias` of `RouteController`

```kotlin
typealias RouteControllerApp<Path, VM, V> = RouteControllerVM<Path, VM, AndroidComposeViewModelProvider, V> // if you don't use Component fo injection
typealias RouteControllerApp<Path, VM, V> = RouteControllerVMC<Path, VM, AndroidComposeViewModelProvider, V, AppComponent> // otherwise
```

If you're not passing arguments to your screen, the definition of `RouteController` remains almost
the same, except for adding `SimpleViewModel` to the RouteController.

```kotlin
class SimplePath: RoutePath // The same

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

#### SimpleViewModel

`SimpleViewModel` has to be inherited from the `AndroidViewModel` class from the [bootstrap](src/main/java/com/speakerboxlite/router/compose/bootstrap/AndroidViewModel.kt) package

```kotlin
class SimpleViewModel(val step: Int, app: Application): AndroidViewModel(app)
```

It's a good practice to create a `BaseViewModel` class.
See example of [BaseViewModel](../sample-compose/src/main/java/com/speakerboxlite/router/samplecompose/base/BaseViewModel.kt)

See other examples here
* [StepViewModel](../sample-compose/src/main/java/com/speakerboxlite/router/samplecompose/step/StepViewModel.kt)
* [StepPath](../sample-compose/src/main/java/com/speakerboxlite/router/samplecompose/step/StepPath.kt)
* [StepView](../sample-compose/src/main/java/com/speakerboxlite/router/samplecompose/step/StepView.kt)

You can find full example of project at [sample-compose](../sample-compose)
