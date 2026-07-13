package com.example.fragment.project.ui.circle

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fragment.project.data.circle.MediaItem
import com.example.fragment.project.data.circle.MediaType

/**
 * 发布动态页面
 *
 * @param onNavigateUp 返回回调
 * @param onPublish 发布成功回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishPostScreen(
    onNavigateUp: () -> Unit = {},
    onPublish: (content: String, mediaList: List<MediaItem>) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val content = remember { mutableStateOf("") }
    val selectedMedia = remember { mutableStateListOf<MediaItem>() }
    var showTopicMenu by remember { mutableStateOf(false) }
    var selectedTopicId by remember { mutableStateOf<String?>(null) }
    var selectedTopicName by remember { mutableStateOf<String?>(null) }
    var location by remember { mutableStateOf<String?>(null) }
    var isPublishing by remember { mutableStateOf(false) }

    val availableTopics = remember {
        listOf(
            "日常" to "daily",
            "美食" to "food",
            "旅行" to "travel",
            "摄影" to "photo",
            "穿搭" to "fashion",
            "美妆" to "beauty",
            "健身" to "fitness",
            "读书" to "reading",
            "游戏" to "gaming",
            "影视" to "movie"
        )
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 9)
    ) { uris ->
        uris.forEach { uri ->
            if (selectedMedia.size < 9) {
                selectedMedia.add(
                    MediaItem(
                        url = uri.toString(),
                        thumbnailUrl = uri.toString(),
                        type = MediaType.IMAGE,
                        mimeType = context.contentResolver.getType(uri) ?: "image/*"
                    )
                )
            }
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            if (selectedMedia.size < 9) {
                selectedMedia.add(
                    MediaItem(
                        url = it.toString(),
                        thumbnailUrl = it.toString(),
                        type = MediaType.VIDEO,
                        mimeType = context.contentResolver.getType(it) ?: "video/*"
                    )
                )
            }
        }
    }

    fun removeMedia(index: Int) {
        if (index in selectedMedia.indices) {
            selectedMedia.removeAt(index)
        }
    }

    fun handlePublish() {
        if (content.value.isBlank() && selectedMedia.isEmpty()) return
        isPublishing = true
        onPublish(content.value, selectedMedia.toList())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "发动态",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { handlePublish() },
                        enabled = !isPublishing && (content.value.isNotBlank() || selectedMedia.isNotEmpty()),
                        modifier = Modifier.padding(end = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isPublishing) "发布中..." else "发布",
                            fontSize = 14.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = content.value,
                onValueChange = { content.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(150.dp),
                placeholder = { Text("分享你的想法...") },
                shape = RoundedCornerShape(8.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            if (selectedMedia.isNotEmpty()) {
                MediaPreviewGrid(
                    mediaList = selectedMedia.toList(),
                    onRemove = { removeMedia(it) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                MediaAddButton(
                    icon = Icons.Default.Image,
                    label = "图片",
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    enabled = selectedMedia.size < 9
                )

                Spacer(modifier = Modifier.width(12.dp))

                MediaAddButton(
                    icon = Icons.Default.Videocam,
                    label = "视频",
                    onClick = {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    enabled = selectedMedia.size < 9
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box {
                    MediaAddButton(
                        icon = Icons.Default.Tag,
                        label = "话题",
                        onClick = { showTopicMenu = true },
                        enabled = true
                    )

                    DropdownMenu(
                        expanded = showTopicMenu,
                        onDismissRequest = { showTopicMenu = false }
                    ) {
                        availableTopics.forEach { (name, _) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    selectedTopicName = name
                                    showTopicMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                MediaAddButton(
                    icon = Icons.Default.LocationOn,
                    label = "位置",
                    onClick = { },
                    enabled = true
                )
            }

            if (selectedTopicName != null) {
                SelectedTopicChip(
                    topicName = selectedTopicName!!,
                    onRemove = { selectedTopicName = null },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (location != null) {
                SelectedLocationChip(
                    location = location!!,
                    onRemove = { location = null },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun MediaPreviewGrid(
    mediaList: List<MediaItem>,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (rowIndex in mediaList.indices step 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (colIndex in 0 until 3) {
                    val itemIndex = rowIndex + colIndex
                    if (itemIndex < mediaList.size) {
                        MediaPreviewItem(
                            media = mediaList[itemIndex],
                            onRemove = { onRemove(itemIndex) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaPreviewItem(
    media: MediaItem,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = media.httpsUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (media.isVideo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "视频",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "移除",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun MediaAddButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (enabled) MaterialTheme.colorScheme.outline else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else Color.Gray
        )
    }
}

@Composable
private fun SelectedTopicChip(
    topicName: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x1A508CEE))
            .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$topicName",
            fontSize = 13.sp,
            color = Color(0xFF508CEE)
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "移除",
                tint = Color(0xFF508CEE),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun SelectedLocationChip(
    location: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x1A33CC99))
            .padding(start = 12.dp, end = 3.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color(0xFF33CC99),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = location,
            fontSize = 13.sp,
            color = Color(0xFF33CC99)
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "移除",
                tint = Color(0xFF33CC99),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
