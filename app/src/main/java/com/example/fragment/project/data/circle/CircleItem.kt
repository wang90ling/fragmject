package com.example.fragment.project.data.circle

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fragment.project.ui.circle.components.CircleMediaGrid
import com.example.fragment.project.utils.TimeUtils

@Composable
fun CircleItem(
    circle: Circle,
    onLikeClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onImageClick: (index: Int) -> Unit = {},
    onVideoClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable(onClick = onUserClick),
                contentAlignment = Alignment.Center
            ) {
                if (circle.userAvatar.isNotBlank()) {
                    AsyncImage(
                        model = circle.userAvatar,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = circle.userName.take(1),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = circle.userName,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = TimeUtils.formatRelativeTime(circle.createTime),
                    color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }

        if (circle.content.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = circle.content,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (circle.hasImages || circle.hasVideo) {
            Spacer(modifier = Modifier.height(12.dp))
            CircleMediaGrid(
                images = circle.images,
                video = circle.video,
                videoThumbnail = circle.videoThumbnail,
                onImageClick = onImageClick,
                onVideoClick = onVideoClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ActionItem(
                    icon = if (circle.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    count = TimeUtils.formatCount(circle.likeCount),
                    isActive = circle.isLiked,
                    activeColor = Color(0xFFFF4757),
                    onClick = onLikeClick
                )
                ActionItem(
                    icon = Icons.Default.ChatBubbleOutline,
                    count = TimeUtils.formatCount(circle.commentCount),
                    onClick = onCommentClick
                )
                ActionItem(
                    icon = Icons.Default.Share,
                    count = TimeUtils.formatCount(circle.shareCount),
                    onClick = {
                        onShareClick()
                        shareContent(context, circle)
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    count: String,
    isActive: Boolean = false,
    activeColor: Color = MaterialTheme.colorScheme.onSecondary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isActive) activeColor else MaterialTheme.colorScheme.onSecondary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = count,
            color = if (isActive) activeColor else MaterialTheme.colorScheme.onSecondary,
            fontSize = 13.sp
        )
    }
}

private fun shareContent(context: Context, circle: Circle) {
    val shareText = buildString {
        append(circle.content)
        if (circle.hasImages) {
            append("\n\n点击查看图片")
        }
        append("\n\n—— 来自圈子的分享")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "分享到"))
}
