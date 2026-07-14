package com.example.fragment.project.ui.circle.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * 圈子媒体网格组件
 * 支持单图、双图、三图、四图、六图、九图以及视频的展示
 */
@Composable
fun CircleMediaGrid(
    images: List<String>,
    video: String? = null,
    videoThumbnail: String? = null,
    onImageClick: (index: Int) -> Unit = {},
    onVideoClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val imageCount = images.size
    val hasVideo = !video.isNullOrBlank()

    when {
        imageCount == 0 && hasVideo -> {
            VideoThumbnail(
                thumbnailUrl = videoThumbnail,
                onClick = onVideoClick,
                modifier = modifier
            )
        }
        imageCount == 1 && !hasVideo -> {
            SingleImage(
                imageUrl = images[0],
                onClick = { onImageClick(0) },
                modifier = modifier
            )
        }
        imageCount == 2 && !hasVideo -> {
            TwoImages(
                images = images,
                onImageClick = onImageClick,
                modifier = modifier
            )
        }
        imageCount == 3 && !hasVideo -> {
            ThreeImages(
                images = images,
                onImageClick = onImageClick,
                modifier = modifier
            )
        }
        imageCount == 4 && !hasVideo -> {
            FourImages(
                images = images,
                onImageClick = onImageClick,
                modifier = modifier
            )
        }
        imageCount in 5..6 && !hasVideo -> {
            SixImages(
                images = images,
                onImageClick = onImageClick,
                modifier = modifier
            )
        }
        imageCount in 7..9 && !hasVideo -> {
            NineImages(
                images = images,
                onImageClick = onImageClick,
                modifier = modifier
            )
        }
        hasVideo && imageCount > 0 -> {
            Column {
                VideoThumbnail(
                    thumbnailUrl = videoThumbnail,
                    onClick = onVideoClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
                if (imageCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    ImageRow(
                        images = images.take(3),
                        onImageClick = onImageClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleImage(
    imageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun TwoImages(
    images: List<String>,
    onImageClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        images.forEachIndexed { index, imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onImageClick(index) },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ThreeImages(
    images: List<String>,
    onImageClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AsyncImage(
            model = images[0],
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onImageClick(0) },
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            images.drop(1).forEachIndexed { index, imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(index + 1) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun FourImages(
    images: List<String>,
    onImageClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            images.take(2).forEachIndexed { index, imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(index) },
                    contentScale = ContentScale.Crop
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            images.drop(2).forEachIndexed { index, imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(index + 2) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun SixImages(
    images: List<String>,
    onImageClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            images.take(3).forEachIndexed { index, imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(index) },
                    contentScale = ContentScale.Crop
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            images.drop(3).forEachIndexed { index, imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(index + 3) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun NineImages(
    images: List<String>,
    onImageClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            images.take(3).forEachIndexed { index, imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(index) },
                    contentScale = ContentScale.Crop
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            images.drop(3).take(3).forEachIndexed { index, imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(index + 3) },
                    contentScale = ContentScale.Crop
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            images.drop(6).forEachIndexed { index, imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(index + 6) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun ImageRow(
    images: List<String>,
    onImageClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        images.forEachIndexed { index, imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onImageClick(index) },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun VideoThumbnail(
    thumbnailUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (!thumbnailUrl.isNullOrBlank()) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2C2C2C))
            )
        }
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "播放",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        }
    }
}
