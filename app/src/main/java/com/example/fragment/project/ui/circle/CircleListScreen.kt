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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

    var selectedCircle by remember { mutableStateOf<Circle?>(null) }
    var showImagePreview by remember { mutableStateOf(false) }
    var previewImageIndex by remember { mutableStateOf(0) }
    var showVideoPlayer by remember { mutableStateOf(false) }
    var showPostScreen by remember { mutableStateOf(false) }
    var showCommentSheet by remember { mutableStateOf(false) }

    val tabItems = listOf("关注", "最新", "最热")
    val pagerState = rememberPagerState(pageCount = { tabItems.size })
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

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

        /**
         * 红框中多余的间距是底部 Tab 栏和列表内容之间的一条空白色带。产生原因是：
         *
         * CircleListScreen.kt 内部使用了一个 Scaffold
         * 外层 MainScreen.kt 的 Scaffold 已经有 bottomBar（高度 80dp + navigationBarsPadding）
         * 内层 Scaffold 的 contentWindowInsets 默认包含 navigationBars 高度（约 84dp）
         * 两层 inset 叠加，导致底部出现约 80-100dp 的额外空白
         * 修复：在 CircleListScreen.kt 的 Scaffold 上添加 contentWindowInsets = WindowInsets(0)，
         * 清除内层 Scaffold 的导航栏 inset，让外层 MainScreen 的 bottomBar 单独控制底部间距，避免双重计算。
         *
         * 这样红框的空白条带会被消除，列表内容会紧贴底部 Tab 栏之上。
         */
        contentWindowInsets = WindowInsets(0),

        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.outlineVariant)
            ) {
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Absolute.Left
                ) {
                    tabItems.forEachIndexed { index, title ->
                        TabItem(
                            title = title,
                            isSelected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
            }
        },
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> CircleListContent(
                    viewModel = viewModel,
                    tabType = CircleTabType.FOLLOW,
                    selectedCircle = selectedCircle,
                    showCommentSheet = showCommentSheet,
                    onSelectCircle = { circle ->
                        selectedCircle = circle
                        viewModel.selectCircle(circle)
                        showCommentSheet = true
                    },
                    onImageClick = { index, circle ->
                        selectedCircle = circle
                        previewImageIndex = index
                        showImagePreview = true
                    },
                    onVideoClick = { circle ->
                        selectedCircle = circle
                        showVideoPlayer = true
                    },
                    onDismissComment = {
                        showCommentSheet = false
                        selectedCircle = null
                        viewModel.clearDetail()
                    }
                )
                1 -> CircleListContent(
                    viewModel = viewModel,
                    tabType = CircleTabType.LATEST,
                    selectedCircle = selectedCircle,
                    showCommentSheet = showCommentSheet,
                    onSelectCircle = { circle ->
                        selectedCircle = circle
                        viewModel.selectCircle(circle)
                        showCommentSheet = true
                    },
                    onImageClick = { index, circle ->
                        selectedCircle = circle
                        previewImageIndex = index
                        showImagePreview = true
                    },
                    onVideoClick = { circle ->
                        selectedCircle = circle
                        showVideoPlayer = true
                    },
                    onDismissComment = {
                        showCommentSheet = false
                        selectedCircle = null
                        viewModel.clearDetail()
                    }
                )
                2 -> CircleListContent(
                    viewModel = viewModel,
                    tabType = CircleTabType.HOTTEST,
                    selectedCircle = selectedCircle,
                    showCommentSheet = showCommentSheet,
                    onSelectCircle = { circle ->
                        selectedCircle = circle
                        viewModel.selectCircle(circle)
                        showCommentSheet = true
                    },
                    onImageClick = { index, circle ->
                        selectedCircle = circle
                        previewImageIndex = index
                        showImagePreview = true
                    },
                    onVideoClick = { circle ->
                        selectedCircle = circle
                        showVideoPlayer = true
                    },
                    onDismissComment = {
                        showCommentSheet = false
                        selectedCircle = null
                        viewModel.clearDetail()
                    }
                )
            }
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

enum class CircleTabType {
    FOLLOW, LATEST, HOTTEST
}

@Composable
private fun TabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 3.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Transparent
                )
        )
    }
}

@Composable
private fun CircleListContent(
    viewModel: CircleViewModel,
    tabType: CircleTabType,
    selectedCircle: Circle?,
    showCommentSheet: Boolean,
    onSelectCircle: (Circle) -> Unit,
    onImageClick: (Int, Circle) -> Unit,
    onVideoClick: (Circle) -> Unit,
    onDismissComment: () -> Unit
) {
    val uiState by viewModel.listState.collectAsStateWithLifecycle()

    SwipeRefreshBox(
        items = uiState.result,
        isRefreshing = uiState.isRefreshing,
        isLoading = uiState.isLoading,
        isFinishing = uiState.isFinishing,
        onRefresh = { viewModel.refresh() },
        onLoad = { viewModel.loadMore() },
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        key = { _, item -> item.id },
    ) { _, item ->
        CircleItem(
            circle = item,
            onLikeClick = { viewModel.likeCircle(item.id) },
            onShareClick = { viewModel.shareCircle(item.id) },
            onCommentClick = {
                onSelectCircle(item)
            },
            onImageClick = { index ->
                onImageClick(index, item)
            },
            onVideoClick = {
                onVideoClick(item)
            },
            onUserClick = { }
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
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
