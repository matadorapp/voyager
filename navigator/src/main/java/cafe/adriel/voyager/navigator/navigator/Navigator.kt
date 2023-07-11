package cafe.adriel.voyager.navigator.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.core.Screen
import cafe.adriel.voyager.navigator.core.ScreenLifecycleStore
import cafe.adriel.voyager.navigator.core.ScreenModelStore
import cafe.adriel.voyager.navigator.core.Stack
import cafe.adriel.voyager.navigator.core.ThreadSafeSet
import cafe.adriel.voyager.navigator.core.rememberScreenLifecycleOwner
import cafe.adriel.voyager.navigator.core.toMutableStateStack

typealias NavigatorContent = @Composable (navigator: Navigator) -> Unit

typealias OnBackPressed = ((currentScreen: Screen) -> Boolean)?

val LocalNavigator: ProvidableCompositionLocal<Navigator?> =
    staticCompositionLocalOf { null }

val <T> ProvidableCompositionLocal<T?>.currentOrThrow: T
    @Composable
    get() = current ?: error("CompositionLocal is null")

@Composable
fun CurrentScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val currentScreen = navigator.lastItem

    navigator.saveableState("currentScreen") {
        currentScreen.Content()
    }
}

@Composable
fun Navigator(
    screen: Screen,
    disposeBehavior: NavigatorDisposeBehavior = NavigatorDisposeBehavior(),
    onBackPressed: OnBackPressed = { true },
    content: NavigatorContent = { CurrentScreen() }
) {
    Navigator(
        screens = listOf(screen),
        disposeBehavior = disposeBehavior,
        onBackPressed = onBackPressed,
        content = content
    )
}

@Composable
fun Navigator(
    screens: List<Screen>,
    disposeBehavior: NavigatorDisposeBehavior = NavigatorDisposeBehavior(),
    onBackPressed: OnBackPressed = { true },
    content: NavigatorContent = { CurrentScreen() }
) {
    require(screens.isNotEmpty()) { "Navigator must have at least one screen" }

    CompositionLocalProvider(
        LocalNavigatorStateHolder providesDefault rememberSaveableStateHolder()
    ) {
        val navigator = rememberNavigator(screens, disposeBehavior, LocalNavigator.current)

        if (navigator.parent?.disposeBehavior?.disposeNestedNavigators != false) {
            NavigatorDisposableEffect(navigator)
        }

        CompositionLocalProvider(
            LocalNavigator provides navigator,
        ) {
            if (disposeBehavior.disposeSteps) {
                StepDisposableEffect(navigator)
            }

            NavigatorBackHandler(navigator, onBackPressed)

            content(navigator)
        }
    }
}

class Navigator internal constructor(
    screens: List<Screen>,
    private val stateHolder: SaveableStateHolder,
    val disposeBehavior: NavigatorDisposeBehavior,
    val parent: Navigator? = null
) : Stack<Screen> by screens.toMutableStateStack(minSize = 1) {

    val level: Int =
        parent?.level?.inc() ?: 0

    val lastItem: Screen by derivedStateOf {
        lastItemOrNull ?: error("Navigator has no screen")
    }

    private val stateKeys = ThreadSafeSet<String>()

    @Deprecated(
        message = "Use 'lastItem' instead. Will be removed in 1.0.0.",
        replaceWith = ReplaceWith("lastItem")
    )
    val last: Screen by derivedStateOf {
        lastItem
    }

    @Composable
    fun saveableState(
        key: String,
        screen: Screen = lastItem,
        content: @Composable () -> Unit
    ) {
        val stateKey = "${screen.key}:$key"
        stateKeys += stateKey

        val lifecycleOwner = rememberScreenLifecycleOwner(screen)
        LifecycleDisposableEffect(lifecycleOwner)
        val hooks = lifecycleOwner.getHooks()

        CompositionLocalProvider(*hooks.providers.toTypedArray()) {
            stateHolder.SaveableStateProvider(stateKey, content = content)
        }
    }

    fun popUntilRoot() {
        popUntilRoot(this)
    }

    private tailrec fun popUntilRoot(navigator: Navigator) {
        navigator.popAll()

        if (navigator.parent != null) {
            popUntilRoot(navigator.parent)
        }
    }

    internal fun dispose(
        screen: Screen
    ) {
        ScreenModelStore.remove(screen)
        ScreenLifecycleStore.remove(screen)
        stateKeys
            .asSequence()
            .filter { it.startsWith(screen.key) }
            .forEach { key ->
                stateHolder.removeState(key)
                stateKeys -= key
            }
    }
}

data class NavigatorDisposeBehavior(
    val disposeNestedNavigators: Boolean = true,
    val disposeSteps: Boolean = true,
)
