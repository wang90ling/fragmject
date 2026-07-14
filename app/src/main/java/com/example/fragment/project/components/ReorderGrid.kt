package com.example.fragment.project.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridLayoutInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 可拖拽排序的 LazyVerticalGrid
 *
 * 实现思路：
 * - draggingItemKey 跨 recompose 稳定跟踪被拖拽元素
 * - draggingOffset 记录从拖拽起点到手指当前位置的位移
 * - 每次触发交换时，draggingOffset 需要补偿一次「旧 slot 中心 -> 新 slot 中心」的差值，
 *   使被拖拽项在视觉上保持在手指下方
 *
 * onMove 回调签名: (fromIndex, toIndex, fromKey, toKey)
 */
@Composable
fun <T> ReorderLazyVerticalGrid(
    items: List<T>,
    key: ((index: Int, item: T) -> Any),
    onMove: (fromIndex: Int, toIndex: Int, fromKey: Any, toKey: Any) -> Unit,
    columns: GridCells,
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    itemContent: @Composable BoxScope.(index: Int, item: T) -> Unit
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val autoScrollThreshold = with(density) { 40.dp.toPx() }

    var draggingItemKey by remember { mutableStateOf<Any?>(null) }
    var draggingItemIndex by remember { mutableStateOf(-1) }
    var draggingOffset by remember { mutableStateOf(Offset.Zero) }

    // 关键：让 pointerInput 协程始终能读到最新的 onMove 和 key 函数
    val currentOnMove by rememberUpdatedState(onMove)
    val currentKey by rememberUpdatedState(key)

    val layoutInfo by remember { derivedStateOf { state.layoutInfo } }

    LazyVerticalGrid(
        columns = columns,
        modifier = modifier.pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = { offset ->
                    val item = layoutInfo.itemInfoAt(offset) ?: return@detectDragGesturesAfterLongPress
                    draggingItemKey = item.key
                    draggingItemIndex = item.index
                    draggingOffset = Offset.Zero
                },
                onDragEnd = { animateEndDrag(scope, draggingOffset) { draggingOffset = it } },
                onDragCancel = { animateEndDrag(scope, draggingOffset) { draggingOffset = it } },
                onDrag = { change, dragAmount ->
                    change.consume()
                    val draggingKey = draggingItemKey ?: return@detectDragGesturesAfterLongPress
                    val draggingIdx = draggingItemIndex

                    // 1. 累积偏移
                    draggingOffset += dragAmount

                    // 2. 边界自动滚动
                    val viewportH = layoutInfo.viewportSize.height.toFloat()
                    val pointerY = change.position.y
                    when {
                        pointerY < autoScrollThreshold ->
                            scope.launch { state.scrollBy(pointerY - autoScrollThreshold) }
                        pointerY > viewportH - autoScrollThreshold ->
                            scope.launch { state.scrollBy(autoScrollThreshold - (viewportH - pointerY)) }
                    }

                    // 3. 命中检测
                    val currentDraggingItem = layoutInfo.visibleItemsInfo.firstOrNull { it.key == draggingKey }
                    val targetInfo = layoutInfo.itemInfoAt(change.position)
                    if (currentDraggingItem == null || targetInfo == null) return@detectDragGesturesAfterLongPress
                    if (targetInfo.key == draggingKey) return@detectDragGesturesAfterLongPress

                    // 4. 计算交换补偿
                    //    交换前被拖拽项在原 slot 中心 + draggingOffset 处
                    //    交换后被拖拽项会去到 target slot 处（数据搬移，layout 还未 recompose）
                    //    所以新位置 = targetInfo.offset + targetInfo.size / 2
                    //    要让元素视觉上保持在原位，需补偿 draggingOffset
                    val currentCenter = currentDraggingItem.offset.toOffset() +
                            currentDraggingItem.size.toOffset() * 0.5f +
                            draggingOffset
                    val newCenter = targetInfo.offset.toOffset() +
                            targetInfo.size.toOffset() * 0.5f

                    // 5. 触发交换
                    currentOnMove(draggingIdx, targetInfo.index, draggingKey, targetInfo.key)
                    draggingItemIndex = targetInfo.index

                    // 6. 补偿偏移：让被拖拽元素视觉上保持在原位
                    draggingOffset = currentCenter - newCenter
                }
            )
        },
        state = state,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled
    ) {
        itemsIndexed(items, currentKey) { index, item ->
            val itemKey = currentKey(index, item)
            val isDragging = draggingItemKey == itemKey
            Box(
                modifier = Modifier
                    .scale(if (isDragging) 0.9f else 1f)
                    .then(
                        if (isDragging) {
                            Modifier
                                .offset {
                                    IntOffset(
                                        draggingOffset.x.roundToInt(),
                                        draggingOffset.y.roundToInt()
                                    )
                                }
                                .zIndex(1f)
                                .shadow(8.dp)
                        } else {
                            Modifier
                                .zIndex(0f)
                                .shadow(0.dp)
                                .animateItem(fadeInSpec = null, fadeOutSpec = null)
                        }
                    )
            ) {
                itemContent(index, item)
            }
        }
    }
}

@Stable
private fun animateEndDrag(
    scope: CoroutineScope,
    from: Offset,
    onUpdate: (Offset) -> Unit,
) {
    scope.launch {
        Animatable(from, Offset.VectorConverter).animateTo(
            targetValue = Offset.Zero,
            animationSpec = spring(
                stiffness = Spring.StiffnessMediumLow,
                visibilityThreshold = Offset.VisibilityThreshold
            )
        ) {
            onUpdate(value)
        }
    }
}

private fun IntOffset.toOffset(): Offset = Offset(x.toFloat(), y.toFloat())

private fun IntSize.toOffset(): Offset = Offset(width.toFloat(), height.toFloat())

private fun LazyGridLayoutInfo.itemInfoAt(hitPoint: Offset): LazyGridItemInfo? =
    visibleItemsInfo.firstOrNull { item ->
        hitPoint.x.toInt() in item.offset.x..item.offset.x + item.size.width &&
                hitPoint.y.toInt() in item.offset.y..item.offset.y + item.size.height
    }