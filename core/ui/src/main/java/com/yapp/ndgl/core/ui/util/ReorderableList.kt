package com.yapp.ndgl.core.ui.util

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun <T> SnapshotStateList<T>.reorder(
    from: Int,
    to: Int,
): Boolean {
    if (from == to || from < 0 || to < 0 || from >= this.size || to >= this.size) return false
    add(to, removeAt(from))
    return true
}

fun Modifier.reorderable(
    state: ReorderableState,
    onUpdateList: (from: Int?, to: Int?) -> Unit = { _, _ -> },
): Modifier = this.pointerInput(state) {
    detectVerticalDragGestures(
        onDragStart = { state.onDragStart(it) },
        onDragEnd = {
            val from = state.dragStartIndex
            val to = state.currentIndex
            state.onDragEnd()
            onUpdateList(from, to)
        },
        onDragCancel = { state.onDragEnd() },
        onVerticalDrag = { _, amount -> state.onDrag(amount) },
    )
}

@Composable
fun <T> rememberReorderableState(
    list: SnapshotStateList<T>,
    lazyListState: LazyListState = rememberLazyListState(),
    offset: Int = 0,
    isReorderable: (key: Any?) -> Boolean = { true },
): ReorderableState {
    val scope = rememberCoroutineScope()
    val currentList by rememberUpdatedState(list)
    val onReorder: (Int, Int) -> Boolean = { from, to ->
        currentList.reorder(from - offset, to - offset)
    }

    return remember {
        ReorderableState(
            scope = scope,
            lazyListState = lazyListState,
            onReorder = onReorder,
            isReorderable = isReorderable,
        )
    }
}

class ReorderableState(
    private val scope: CoroutineScope,
    val lazyListState: LazyListState,
    private val onReorder: (Int, Int) -> Boolean,
    private val isReorderable: (key: Any?) -> Boolean = { true },
) {
    // 드래그 시작한 아이템 상하단 y값
    private var initialYBounds by mutableStateOf(0 to 0)

    // 드래그 거리
    private var distance by mutableFloatStateOf(0f)

    // 드래그 중인 아이템 정보
    private var info: LazyListItemInfo? by mutableStateOf(null)

    // 시작 상하단 y값 + 드래그 거리
    private val currentYBounds: Pair<Int, Int> get() = initialYBounds.let { (topY, bottomY) -> topY + distance.toInt() to bottomY + distance.toInt() }

    // 순서 변경 임계값
    private val threshold: Int get() = initialYBounds.let { (it.first + it.second) / 2 + distance.toInt() }

    // 드래그 중인 아이템의 변화하는 인덱스
    val currentIndex: Int? get() = info?.index

    // 드래그 시작 시점의 원본 LazyColumn 인덱스
    var dragStartIndex: Int? = null
        private set

    // 오토 스크롤 Job
    private var autoScrollJob by mutableStateOf<Job?>(null)

    // 드래그 시작 - isReorderable이 false인 아이템은 드래그 불가
    fun onDragStart(offset: Offset) {
        lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() > item.offset && offset.y.toInt() < (item.offset + item.size)
            }
            ?.takeIf { isReorderable(it.key) }
            ?.let {
                initialYBounds = it.offset to (it.offset + it.size)
                info = it
                dragStartIndex = it.index
            }
    }

    // 아이템 인덱스 업데이트
    private fun updateItemIndex() {
        val itemInfo = info ?: return
        when {
            currentYBounds.first < itemInfo.offset && threshold < itemInfo.offset ->
                tryMoveUp(itemInfo)
            currentYBounds.second > itemInfo.offset + itemInfo.size && threshold > itemInfo.offset + itemInfo.size ->
                tryMoveDown(itemInfo)
        }
    }

    // 위로 드래그할 때
    private fun tryMoveUp(itemInfo: LazyListItemInfo) {
        val target = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemInfo.index - 1 }
        if (target != null) {
            // 대상 아이템이 reorderable일 때만 이동
            if (isReorderable(target.key) && onReorder(itemInfo.index, itemInfo.index - 1)) {
                info = target
            }
        } else {
            // 오토 스크롤 중 아이템이 화면에 보이지 않을 때
            val firstItem = lazyListState.layoutInfo.visibleItemsInfo.first()
            if (isReorderable(firstItem.key) && onReorder(itemInfo.index, firstItem.index)) {
                info = lazyListState.layoutInfo.visibleItemsInfo.first()
            }
        }
    }

    // 아래로 드래그할 때
    private fun tryMoveDown(itemInfo: LazyListItemInfo) {
        val target = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == itemInfo.index + 1 }
        if (target != null) {
            if (onReorder(itemInfo.index, itemInfo.index + 1)) {
                info = target
            }
        } else {
            // 오토 스크롤 중 아이템이 화면에 보이지 않을 때
            val lastItem = lazyListState.layoutInfo.visibleItemsInfo.last()
            if (onReorder(itemInfo.index, lastItem.index)) {
                info = lazyListState.layoutInfo.visibleItemsInfo.last()
            }
        }
    }

    // 드래그 중
    fun onDrag(amount: Float) {
        if (dragStartIndex == null) return
        distance += amount
        updateItemIndex()
        onAutoScroll()
    }

    // 드래그 종료
    fun onDragEnd() {
        initialYBounds = 0 to 0
        distance = 0f
        info = null
        dragStartIndex = null
        autoScrollJob?.cancel()
        autoScrollJob = null
    }

    // 오토 스크롤
    private fun onAutoScroll() {
        if (autoScrollJob?.isActive == true) return
        autoScrollJob = scope.launch(Dispatchers.Main) {
            while (true) {
                val scrollOffset = when {
                    currentYBounds.first < lazyListState.layoutInfo.viewportStartOffset -> -16f
                    currentYBounds.second > lazyListState.layoutInfo.viewportEndOffset -> 16f
                    else -> null
                }

                scrollOffset?.let {
                    lazyListState.scrollBy(it)
                    delay(8)
                } ?: break
            }
        }
    }
}
