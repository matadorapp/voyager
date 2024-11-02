package cafe.adriel.voyager.navigator.bottomSheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.core.Screen
import cafe.adriel.voyager.navigator.core.ScreenKey
import cafe.adriel.voyager.navigator.core.Stack
import cafe.adriel.voyager.navigator.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.navigator.Navigator
import cafe.adriel.voyager.navigator.navigator.compositionUniqueId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

typealias BottomSheetNavigatorContent = @Composable (bottomSheetNavigator: BottomSheetNavigator) -> Unit

val LocalBottomSheetNavigator: ProvidableCompositionLocal<BottomSheetNavigator> =
    staticCompositionLocalOf { error("BottomSheetNavigator not initialized") }

@ExperimentalMaterial3Api
@Composable
fun BottomSheetNavigator(
    modifier: Modifier = Modifier,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = BottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    skipHalfExpanded: Boolean = true,
    key: String = compositionUniqueId(),
    sheetContent: BottomSheetNavigatorContent = { CurrentScreen() },
    content: BottomSheetNavigatorContent
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipHalfExpanded,
    )

    Navigator(
        screen = HiddenBottomSheetScreen,
        onBackPressed = null,
        key = key
    ) { navigator ->
        val bottomSheetNavigator = remember(navigator, sheetState, coroutineScope) {
            BottomSheetNavigator(navigator, sheetState, coroutineScope)
        }

        CompositionLocalProvider(LocalBottomSheetNavigator provides bottomSheetNavigator) {
            content(bottomSheetNavigator)

            if (sheetState.isVisible) {
                ModalBottomSheet(
                    modifier = modifier,
                    scrimColor = scrimColor,
                    sheetState = sheetState,
                    shape = sheetShape,
                    tonalElevation = sheetElevation,
                    containerColor = sheetBackgroundColor,
                    contentColor = sheetContentColor,
                    dragHandle = null,
                    onDismissRequest = {
                        bottomSheetNavigator.hide()
                    },
                    content = {
                        sheetContent(bottomSheetNavigator)
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class BottomSheetNavigator internal constructor(
    private val navigator: Navigator,
    private val sheetState: SheetState,
    private val coroutineScope: CoroutineScope
) : Stack<Screen> by navigator {

    val isVisible: Boolean
        get() = sheetState.isVisible

    fun show(screen: Screen) {
        coroutineScope.launch {
            replaceAll(screen)
            sheetState.show()
        }
    }

    fun hide() {
        coroutineScope.launch {
            if (isVisible) {
                sheetState.hide()
                replaceAll(HiddenBottomSheetScreen)
            }
        }
    }
}

private object HiddenBottomSheetScreen : Screen {

    @Composable
    override fun Content() {
        Spacer(modifier = Modifier.height(0.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun BottomSheetNavigatorPreview(
    modifier: Modifier = Modifier
) {

    BottomSheetNavigator(
        content = {
            val bsNavigator = LocalBottomSheetNavigator.current
            Column {
                Text(
                    "hello",
                    color = Color.White
                )
                Button(
                    onClick = {
                        bsNavigator.show(
                            object : Screen {
                                override val key: ScreenKey
                                    get() = Math.random().toString()

                                @Composable
                                override fun Content() {
                                    Column {
                                        (0..100).forEach {
                                            Text(
                                                it.toString(),
                                                color = Color.Green
                                            )
                                        }
                                    }
                                }

                            }
                        )
                    }
                ) {
                    Text(
                        "Click me",
                        color = Color.White
                    )
                }
            }
        }
    )
}
