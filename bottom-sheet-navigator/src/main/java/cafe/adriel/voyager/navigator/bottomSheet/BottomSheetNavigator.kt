package cafe.adriel.voyager.navigator.bottomSheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.core.Screen
import cafe.adriel.voyager.navigator.core.Stack
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
    hideOnBackPress: Boolean = true,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = BottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    skipHalfExpanded: Boolean = true,
    key: String = compositionUniqueId(),
    content: BottomSheetNavigatorContent
) {
    var hideBottomSheet by remember { mutableStateOf<BottomSheetNavigator?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipHalfExpanded,
        confirmValueChange = { state ->
            if (state == SheetValue.Hidden) {
                hideBottomSheet?.hide()
            }
            true
        },
    )

    Navigator(
        screen = HiddenBottomSheetScreen,
        onBackPressed = null,
        key = key
    ) { navigator ->
        val bottomSheetNavigator = remember(navigator, sheetState, coroutineScope) {
            BottomSheetNavigator(navigator, sheetState, coroutineScope)
                .apply {
                    hideBottomSheet = this
                }
        }

        CompositionLocalProvider(LocalBottomSheetNavigator provides bottomSheetNavigator) {
            ModalBottomSheet(
                modifier = modifier,
                scrimColor = scrimColor,
                sheetState = sheetState,
                shape = sheetShape,
                tonalElevation = sheetElevation,
                containerColor = sheetBackgroundColor,
                contentColor = sheetContentColor,
                onDismissRequest = {
                    hideBottomSheet?.hide()
                },
                content = {
                    BackHandler(enabled = sheetState.isVisible) {
                        if (bottomSheetNavigator.pop().not() && hideOnBackPress) {
                            bottomSheetNavigator.hide()
                        }
                    }
                    content(bottomSheetNavigator)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class BottomSheetNavigator internal constructor(
    private val navigator: Navigator,
    private val sheetState: SheetState,
    private val coroutineScope: CoroutineScope
) : Stack<Screen> by navigator {

    public val isVisible: Boolean
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
        Spacer(modifier = Modifier.height(1.dp))
    }
}
