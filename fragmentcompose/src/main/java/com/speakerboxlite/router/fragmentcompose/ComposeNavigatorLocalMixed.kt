package com.speakerboxlite.router.fragmentcompose

import androidx.compose.runtime.Composable
import com.speakerboxlite.router.RoutePath
import com.speakerboxlite.router.RoutePathResult
import com.speakerboxlite.router.Router
import com.speakerboxlite.router.RouterResultDispatcher
import com.speakerboxlite.router.ViewResult
import com.speakerboxlite.router.compose.CommandExecutorFactory
import com.speakerboxlite.router.compose.ComposeNavigatorContent
import com.speakerboxlite.router.compose.ComposeNavigatorLocal
import com.speakerboxlite.router.compose.LocalRouter
import com.speakerboxlite.router.compose.LocalViewKey
import com.speakerboxlite.router.compose.SingleContent
import com.speakerboxlite.router.compose.currentOrThrow

@Composable
fun ComposeNavigatorLocalMixed(
    key: String = LocalViewKey.currentOrThrow,
    router: Router = LocalRouter.currentOrThrow,
    path: RoutePath?,
    executorFactory: CommandExecutorFactory = CommandExecutorFactory { CommandExecutorComposeLocalMixed(it) },
    content: ComposeNavigatorContent = { router, navigator -> SingleContent(router, navigator) })
{
    ComposeNavigatorLocal(
        key = key,
        router = router,
        path = path,
        executorFactory = executorFactory,
        content = content)
}

@Composable
fun <VR: ViewResult, R: Any> ComposeNavigatorLocalMixed(
    key: String = LocalViewKey.currentOrThrow,
    router: Router = LocalRouter.currentOrThrow,
    viewResult: VR,
    path: RoutePathResult<R>?,
    result: RouterResultDispatcher<VR, R>,
    executorFactory: CommandExecutorFactory = CommandExecutorFactory { CommandExecutorComposeLocalMixed(it) },
    content: ComposeNavigatorContent = { router, navigator -> SingleContent(router, navigator) })
{
    ComposeNavigatorLocal(
        key = key,
        router = router,
        viewResult = viewResult,
        path = path,
        result = result,
        executorFactory = executorFactory,
        content = content)
}