package com.example.fragment.project.ui.circle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fragment.project.AppTheme
import com.example.fragment.project.components.SwipeRefreshBox
import com.example.fragment.project.data.circle.Circle
import com.example.fragment.project.data.circle.CircleComment
import com.example.fragment.project.data.circle.CircleItem
import com.example.fragment.project.ui.circle.components.ImagePreviewScreen
import com.example.fragment.project.ui.circle.components.VideoPlayerScreen
import com.example.fragment.project.utils.TimeUtils

/**
 * 圈子列表展示界面
 * 支持：时间格式化、图片Grid展示、图片预览、视频播放、点赞、分享、评论、发布动态
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircleListScreen(
    viewModel: CircleViewModel = viewModel(),
    onNavigate: (route: Any) -> Unit = {},
    onNavigateUp: () -> Unit = {},
) {
    val uiState by viewModel.listState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedCircle by remember { mutableStateOf<Circle?>(null) }
    var showImagePreview by remember { mutableStateOf(false) }
    var previewImageIndex by remember { mutableStateOf(0) }
    var showVideoPlayer by remember { mutableStateOf(false) }
    var showPostScreen by remember { mutableStateOf(false) }
    var showCommentSheet by remember { mutableStateOf(false) }

    if (showPostScreen) {
        PostCircleScreen(
            viewModel = viewModel,
            onNavigateUp = { showPostScreen = false },
            onPostSuccess = { showPostScreen = false }
        )
        return
    }

    if (showImagePreview && selectedCircle != null) {
        ImagePreviewScreen(
            images = selectedCircle!!.images,
            initialPage = previewImageIndex,
            onDismiss = {
                showImagePreview = false
                selectedCircle = null
            }
        )
        return
    }

    if (showVideoPlayer && selectedCircle?.video != null) {
        VideoPlayerScreen(
            videoUrl = selectedCircle!!.video!!,
            onDismiss = {
                showVideoPlayer = false
                selectedCircle = null
            }
        )
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showPostScreen = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "发布动态",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        SwipeRefreshBox(
            items = uiState.result,
            isRefreshing = uiState.isRefreshing,
            isLoading = uiState.isLoading,
            isFinishing = uiState.isFinishing,
            onRefresh = { viewModel.refresh() },
            onLoad = { viewModel.loadMore() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            key = { _, item -> item.id },
        ) { _, item ->
            CircleItem(
                circle = item,
                onLikeClick = { viewModel.likeCircle(item.id) },
                onShareClick = { viewModel.shareCircle(item.id) },
                onCommentClick = {
                    selectedCircle = item
                    viewModel.selectCircle(item)
                    showCommentSheet = true
                },
                onImageClick = { index ->
                    selectedCircle = item
                    previewImageIndex = index
                    showImagePreview = true
                },
                onVideoClick = {
                    selectedCircle = item
                    showVideoPlayer = true
                },
                onUserClick = { }
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
    }

    if (showCommentSheet && selectedCircle != null) {
        CommentBottomSheet(
            circle = selectedCircle!!,
            viewModel = viewModel,
            onDismiss = {
                showCommentSheet = false
                selectedCircle = null
                viewModel.clearDetail()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentBottomSheet(
    circle: Circle,
    viewModel: CircleViewModel,
    onDismiss: () -> Unit,
) {
    val detailState by viewModel.detailState.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Text(
                text = "评论",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp)
            )

            if (circle.content.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = circle.userName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = circle.content,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (detailState.comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无评论，快来抢沙发吧~",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .padding(horizontal = 16.dp)
                ) {
                    detailState.comments.forEach { comment ->
                        CommentItem(
                            comment = comment,
                            onReplyClick = { }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = detailState.commentText,
                    onValueChange = { viewModel.updateCommentText(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("写评论...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = {
                        if (detailState.commentText.isNotBlank()) {
                            viewModel.postComment()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "发送",
                        tint = if (detailState.commentText.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: CircleComment,
    onReplyClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            if (comment.userAvatar.isNotBlank()) {
                coil.compose.AsyncImage(
                    model = comment.userAvatar,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = comment.userName.take(1),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = comment.userName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.content,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = TimeUtils.formatRelativeTime(comment.createTime),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (comment.replyCount > 0) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "${comment.replyCount}条回复",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(onClick = onReplyClick)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun CircleListScreenPreview() {
    AppTheme {
        CircleListScreen()
    }
}
