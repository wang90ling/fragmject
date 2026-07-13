package com.example.fragment.project.components.circle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fragment.project.data.circle.MediaItem

/**
 * 媒体网格组件 - 支持图片和视频
 *
 * 布局规则：
 * - 0个：无内容
 * - 1个：单图显示，稍大
 * - 2个：左右各一张
 * - 3个：上下排列（1上2下）
 * - 4个：上下各两张
 * - 6个：上下各三张
 * - 9个：三行三列
 *
 * @param mediaList 媒体列表
 * @param onImageClick 点击图片回调 (index)
 * @param onVideoClick 点击视频回调 (index)
 * @param onAddClick 点击添加按钮回调
 * @param showAddButton 是否显示添加按钮
 */
@Composable
fun MediaGrid(
    mediaList: List<MediaItem>,
    onImageClick: (Int) -> Unit = {},
    onVideoClick: (Int) -> Unit = {},
    onAddClick: () -> Unit = {},
    showAddButton: Boolean = false,
    modifier: Modifier = Modifier,
    maxCount: Int = 9,
) {
    val displayList = if (mediaList.size > maxCount) {
        mediaList.take(maxCount)
    } else {
        mediaList
    }

    if (displayList.isEmpty() && !showAddButton) return

    val gridConfig = calculateGridConfig(displayList.size, showAddButton)
    val columns = gridConfig.columns
    val rows = gridConfig.rows

    if (columns == 1 && rows == 1 && displayList.size == 1) {
        SingleMediaItem(
            media = displayList[0],
            onImageClick = { onImageClick(0) },
            onVideoClick = { onVideoClick(0) },
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (rowIndex in 0 until rows) {
            val rowItems = getRowItems(displayList, rowIndex, rows, columns, showAddButton)
            if (rowItems.isNotEmpty()) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rowItems.forEach { item ->
                        when (item) {
                            is GridItem.MediaItemData -> {
                                MediaGridItem(
                                    media = item.media,
                                    index = item.index,
                                    onImageClick = { onImageClick(item.index) },
                                    onVideoClick = { onVideoClick(item.index) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(getItemAspectRatio(displayList.size, rowIndex, item.index, columns, rows))
                                )
                            }
                            is GridItem.AddButton -> {
                                AddMediaButton(
                                    onClick = onAddClick,
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }
                    if (rowItems.size < columns) {
                        val remaining = columns - rowItems.size
                        repeat(remaining) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleMediaItem(
    media: MediaItem,
    onImageClick: () -> Unit,
    onVideoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                if (media.isVideo) onVideoClick() else onImageClick()
            }
    ) {
        AsyncImage(
            model = if (media.isVideo) media.httpsThumbnailUrl else media.httpsUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (media.isVideo) {
            VideoOverlay(duration = media.durationText)
        }
    }
}

@Composable
private fun MediaGridItem(
    media: MediaItem,
    index: Int,
    onImageClick: () -> Unit,
    onVideoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                if (media.isVideo) onVideoClick() else onImageClick()
            }
    ) {
        AsyncImage(
            model = if (media.isVideo) media.httpsThumbnailUrl else media.httpsUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        if (media.isVideo) {
            VideoOverlay(duration = media.durationText)
        }
    }
}

@Composable
private fun VideoOverlay(duration: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "播放",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            if (duration.isNotBlank()) {
                Text(
                    text = duration,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AddMediaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Add,
                contentDescription = "添加",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

private sealed class GridItem {
    data class MediaItemData(val media: MediaItem, val index: Int) : GridItem()
    data object AddButton : GridItem()
}

private data class GridConfig(val columns: Int, val rows: Int)

private fun calculateGridConfig(size: Int, showAddButton: Boolean): GridConfig {
    val total = if (showAddButton) size + 1 else size
    return when {
        total <= 1 -> GridConfig(1, 1)
        total <= 2 -> GridConfig(2, 1)
        total <= 3 -> GridConfig(3, 1)
        total <= 4 -> GridConfig(2, 2)
        total <= 6 -> GridConfig(3, 2)
        total <= 9 -> GridConfig(3, 3)
        else -> GridConfig(3, (total + 2) / 3)
    }
}

private fun getRowItems(
    mediaList: List<MediaItem>,
    rowIndex: Int,
    rows: Int,
    columns: Int,
    showAddButton: Boolean
): List<GridItem> {
    val result = mutableListOf<GridItem>()
    val startIndex = rowIndex * columns
    val endIndex = minOf(startIndex + columns, mediaList.size)

    for (i in startIndex until endIndex) {
        result.add(GridItem.MediaItemData(mediaList[i], i))
    }

    if (showAddButton && rowIndex == rows - 1 && mediaList.size < 9) {
        result.add(GridItem.AddButton)
    }

    return result
}

private fun getItemAspectRatio(
    totalCount: Int,
    rowIndex: Int,
    itemIndex: Int,
    columns: Int,
    rows: Int
): Float {
    val isFirstRow = rowIndex == 0
    val isLastRow = rowIndex == rows - 1
    val isFirstCol = itemIndex % columns == 0
    val isLastCol = itemIndex % columns == columns - 1

    return when {
        totalCount == 1 -> 1.2f
        totalCount == 2 -> 1f
        totalCount == 3 && isFirstRow -> 2f
        totalCount == 4 && rows == 2 -> 1f
        totalCount == 6 && rows == 2 -> 1f
        totalCount == 9 -> 1f
        else -> 1f
    }
}
