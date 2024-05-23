# Router-Android

Provides an easy solution for screen navigation in the Android, eliminating the need to understand the Fragment
lifecycle or implement transitions in Compose. Also, supports mixed transitions
between Fragments and Compose screens if you are migrating from the Fragments to Compose or have mixed
project at all.&#x20;

Latest release verion:

[![](https://jitpack.io/v/AlexExiv/Router-Android.svg)](https://jitpack.io/#AlexExiv/Router-Android)

[Full documentation](https://android-navigation.modelmanager.dev/)

## Goals

1. Project decoupling. Keep the business logic separate from the navigation logic, maintaining a clean architecture.
2. Easy navigation between screens
3. Send parameters to the screen you want to navigate to and get a result from its job
4. Intercept navigation and replace it by a new one if needed. For example signin screen for unauthorized users
5. Opportunity to close a sequence of screens united in a group. For example you have wizard with several steps and you need to close it all once a user completes his journey
6. Deep links
7. Happy life

## Philosophy

The main idea of Router is to avoid navigation calls like these:

```kotlin
// show new fragment in the parent Fragment
parentFragment?.showFragment(NewFragment.newInstance())

// show dialog
showDialog(NewDialogFragment.newInstance())

// or change tab
(parentFragment as TabsFragment).changeTab(0)

// or another way to change tab
App.shared.mainActivity.changeTab(0)

//or whatever it is
```

By doing so, you add extra dependencies between screens. Instead, navigation should be handled in a way that the screen only knows it needs to navigate, but not the specifics of how that navigation is performed. This keeps the business logic separate from the navigation logic, maintaining a clean architecture.

You should only call `router.route(ScreenPath())`, and the router will handle displaying the screen as it should be shown. It will change the selected tab, close to the screen if it exists in the stack, or present it in full screen.

## Features

1. Navigation between screens
2. Transfer result between screens
3. Middleware intercepts navigation between screens, blocking it or replacing it with another navigation
4. Chain - united sequence of screens

## Example

The simple example of creating a Route for navigation. Here, ScreenView is a pseudo abstract class. For actual implementations, please refer to:
* [sample-fragment](sample-fragment) : Example project with fragments
* [sample-compose](sample-compose) : Example project with Compose
* [sample-mixed](sample-mixed) : Example mixed project containing fragments and Compose
* [sample-hilt](sample-hilt) : Example project with Compose and Hilt injection

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

## Documentation

[Full documentation](https://android-navigation.modelmanager.dev/)
