package com.example.fragment.project.ui.circle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fragment.project.AppTheme
import com.example.fragment.project.CircleCommentRoute
import com.example.fragment.project.CircleShareRoute
import com.example.fragment.project.CircleUserRoute
import com.example.fragment.project.CircleVideoRoute
import com.example.fragment.project.components.circle.CirclePostCard
import com.example.fragment.project.components.SwipeRefreshBox
import com.example.fragment.project.components.TitleBar
import com.example.fragment.project.data.circle.MediaItem

/**
 * 圈子动态列表页面
 */
@Composable
fun CircleListScreen(
    viewModel: CircleViewModel = viewModel(),
    onNavigate: (route: Any) -> Unit = {},
    onNavigateUp: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedImageIndex by rememberSaveable { mutableIntStateOf(0) }
    var showImagePreview by remember { mutableStateOf(false) }
    var imageList by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var showPublishSheet by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showPublishSheet = true },
                    containerColor = AppTheme.blue,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "发动态"
                    )
                }
            }
        ) { innerPadding ->
            SwipeRefreshBox(
                items = uiState.result,
                isRefreshing = uiState.isRefreshing,
                isLoading = uiState.isLoading,
                isFinishing = uiState.isFinishing,
                onRefresh = { viewModel.getHome() },
                onLoad = { viewModel.getNext() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                key = { _, item -> item.id },
            ) { _, item ->
                CirclePostCard(
                    post = item,
                    onUserClick = { userId ->
                        onNavigate(CircleUserRoute(userId))
                    },
                    onImageClick = { index ->
                        selectedImageIndex = index
                        imageList = item.mediaUrls.filter { it.isImage }
                        showImagePreview = true
                    },
                    onVideoClick = { index ->
                        val video = item.mediaUrls.filter { it.isVideo }.getOrNull(index)
                        video?.let {
                            onNavigate(CircleVideoRoute(it.url, it.thumbnailUrl))
                        }
                    },
                    onLikeClick = {
                        viewModel.likePost(item.id)
                    },
                    onCommentClick = {
                        onNavigate(CircleCommentRoute(item.id))
                    },
                    onShareClick = {
                        onNavigate(CircleShareRoute(item.id))
                    },
                    onMoreClick = {
                        showMoreMenu = true
                    }
                )
            }
        }

        if (showMoreMenu) {
            AlertDialog(
                onDismissRequest = { showMoreMenu = false },
                title = { Text("选择操作") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = { showMoreMenu = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("收藏")
                        }
                        TextButton(
                            onClick = { showMoreMenu = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("举报")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showMoreMenu = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }

    if (showImagePreview && imageList.isNotEmpty()) {
        ImagePreviewScreen(
            mediaList = imageList,
            initialIndex = selectedImageIndex,
            onNavigateUp = { showImagePreview = false }
        )
    }

    if (showPublishSheet) {
        PublishPostScreen(
            onNavigateUp = { showPublishSheet = false },
            onPublish = { content, mediaList ->
                viewModel.publishPost(content, mediaList)
                showPublishSheet = false
            }
        )
    }
}
