package cafe.adriel.voyager.navigator.transitions

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.core.Screen
import cafe.adriel.voyager.navigator.navigator.Navigator

typealias ScreenTransitionContent = @Composable AnimatedVisibilityScope.(Screen) -> Unit

@ExperimentalAnimationApi
@Composable
fun ScreenTransition(
    navigator: Navigator,
    transition: AnimatedContentTransitionScope<Screen>.() -> ContentTransform,
    modifier: Modifier = Modifier,
    content: ScreenTransitionContent = { it.Content() }
) {
    AnimatedContent(
        targetState = navigator.lastItem,
        transitionSpec = transition,
        modifier = modifier
    ) { screen ->
        navigator.saveableState("transition", screen) {
            content(screen)
        }
    }
}
