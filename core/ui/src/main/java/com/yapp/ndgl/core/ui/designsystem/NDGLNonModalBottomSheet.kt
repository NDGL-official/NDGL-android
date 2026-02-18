package com.yapp.ndgl.core.ui.designsystem

import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import com.yapp.ndgl.core.ui.util.consumeSwipeWithinBottomSheetBoundsNestedScrollConnection
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class NDGLNonModalBottomSheetState<T>(
    val anchoredDraggableState: AnchoredDraggableState<T>,
)

@Composable
fun <T> rememberNDGLNonModalBottomSheetState(
    initialValue: T,
    anchors: DraggableAnchors<T>,
): NDGLNonModalBottomSheetState<T> {
    val draggableState = remember(anchors) {
        AnchoredDraggableState(
            initialValue = initialValue,
            anchors = anchors,
        )
    }

    return remember(draggableState) {
        NDGLNonModalBottomSheetState(draggableState)
    }
}

@Composable
fun <T> NDGLNonModalBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: NDGLNonModalBottomSheetState<T>,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = sheetState.anchoredDraggableState,
        positionalThreshold = { distance -> distance * 0.5f },
    )
    val nestedScrollConnection = consumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
        anchoredDraggableState = sheetState.anchoredDraggableState,
        orientation = Orientation.Vertical,
        onFling = { velocity ->
            coroutineScope.launch {
                sheetState.anchoredDraggableState.anchoredDrag {
                    val scrollFlingScope = object : ScrollScope {
                        override fun scrollBy(pixels: Float): Float {
                            dragTo(sheetState.anchoredDraggableState.offset + pixels)
                            return pixels
                        }
                    }
                    with(flingBehavior) {
                        scrollFlingScope.performFling(velocity)
                    }
                }
            }
        },
    )

    Box(
        modifier = modifier
            .offset {
                IntOffset(0, sheetState.anchoredDraggableState.offset.roundToInt())
            }
            .anchoredDraggable(
                state = sheetState.anchoredDraggableState,
                orientation = Orientation.Vertical,
                flingBehavior = flingBehavior,
            )
            .nestedScroll(nestedScrollConnection),
    ) {
        content.invoke()
    }
}
