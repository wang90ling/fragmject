package com.example.fragment.project.ui.circle

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fragment.project.AppTheme
import com.example.fragment.project.components.ReorderLazyVerticalGrid
import com.example.miaow.picture.selector.PictureSelectorActivity
import com.example.miaow.picture.selector.bean.MediaBean
import kotlinx.coroutines.launch

private const val MAX_IMAGE_COUNT = 9
private const val MAX_VIDEO_SIZE_BYTES = 50L * 1024 * 1024

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCircleScreen(
    viewModel: CircleViewModel = remember { CircleViewModel() },
    onNavigateUp: () -> Unit = {},
    onPostSuccess: () -> Unit = {}
) {
    val state by viewModel.postState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showMediaPicker by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetPostState()
        }
    }

    val imageSelectorLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val selectedImages = mutableListOf<String>()
            if (intent != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra<MediaBean>("data")?.forEach { media ->
                    selectedImages.add(media.uri.toString())
                }
            } else {
                intent?.getParcelableArrayListExtra<MediaBean>("data")?.forEach { media ->
                    selectedImages.add(media.uri.toString())
                }
            }
            val remainingSlots = MAX_IMAGE_COUNT - state.images.size
            selectedImages.take(remainingSlots).forEach { path ->
                viewModel.addPostImage(path)
            }
            if (selectedImages.size > remainingSlots && remainingSlots > 0) {
                Toast.makeText(context, "最多只能选择${MAX_IMAGE_COUNT}张图片", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                @Suppress("DEPRECATION")
                intent.getParcelableArrayListExtra<MediaBean>("data")?.firstOrNull()?.let { media ->
                    val size = try {
                        context.contentResolver.openFileDescriptor(media.uri, "r")?.use { it.statSize } ?: -1L
                    } catch (e: Exception) {
                        -1L
                    }
                    if (size > 0 && size > MAX_VIDEO_SIZE_BYTES) {
                        Toast.makeText(
                            context,
                            "视频大小不能超过50MB",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.setPostVideo(media.uri.toString())
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                intent?.getParcelableArrayListExtra<MediaBean>("data")?.firstOrNull()?.let { media ->
                    val size = try {
                        context.contentResolver.openFileDescriptor(media.uri, "r")?.use { it.statSize } ?: -1L
                    } catch (e: Exception) {
                        -1L
                    }
                    if (size > 0 && size > MAX_VIDEO_SIZE_BYTES) {
                        Toast.makeText(
                            context,
                            "视频大小不能超过50MB",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.setPostVideo(media.uri.toString())
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("发布动态") },
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
                        onClick = {
                            viewModel.postCircle()
                            onPostSuccess()
                        },
                        enabled = !state.isPosting && (state.content.isNotBlank() || state.images.isNotEmpty() || state.videoPath != null),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (state.isPosting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("发布中...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("发布")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                BasicTextField(
                    value = state.content,
                    onValueChange = { viewModel.updatePostContent(it) },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        Box {
                            if (state.content.isEmpty()) {
                                Text(
                                    text = "分享你的想法...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.images.isNotEmpty()) {
                Text(
                    text = "图片 (${state.images.size}/$MAX_IMAGE_COUNT)  长按拖动排序",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                ReorderLazyVerticalGrid(
                    items = state.images,
                    key = { _, path -> path },
                    onMove = { fromIndex, toIndex, _, _ ->
                        viewModel.movePostImage(fromIndex, toIndex)
                    },
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(
                        ((state.images.size / 3 + if (state.images.size % 3 == 0) 0 else 1) * 110).dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) { _, imagePath ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = imagePath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { viewModel.removePostImage(imagePath) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(24.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "删除",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (state.images.size < MAX_IMAGE_COUNT) {
                if (state.images.isNotEmpty()) Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            val intent = Intent(context, PictureSelectorActivity::class.java)
                            imageSelectorLauncher.launch(intent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "添加图片",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            if (state.videoPath != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "视频",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2C2C2C)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(40.dp)
                    )
                    IconButton(
                        onClick = { viewModel.setPostVideo(null) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "删除视频",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    icon = Icons.Default.Image,
                    text = if (state.images.isEmpty()) "添加图片" else "继续添加",
                    onClick = {
                        if (state.images.size < MAX_IMAGE_COUNT) {
                            val intent = Intent(context, PictureSelectorActivity::class.java)
                            imageSelectorLauncher.launch(intent)
                        }
                    },
                    enabled = state.images.size < MAX_IMAGE_COUNT
                )
                Spacer(modifier = Modifier.width(16.dp))
                ActionButton(
                    icon = Icons.Default.Videocam,
                    text = if (state.videoPath == null) "添加视频" else "更换视频",
                    onClick = {
                        val intent = Intent(context, PictureSelectorActivity::class.java)
                        videoPickerLauncher.launch(intent)
                    },
                    enabled = true
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "提示：最多可添加${MAX_IMAGE_COUNT}张图片，视频不超过50MB",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }

    if (showMediaPicker) {
        ModalBottomSheet(
            onDismissRequest = { showMediaPicker = false },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "选择类型",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MediaOption(
                        icon = Icons.Default.Image,
                        text = "图片",
                        onClick = {
                            scope.launch {
                                bottomSheetState.hide()
                                showMediaPicker = false
                                val intent = Intent(context, PictureSelectorActivity::class.java)
                                imageSelectorLauncher.launch(intent)
                            }
                        }
                    )
                    MediaOption(
                        icon = Icons.Default.Videocam,
                        text = "视频",
                        onClick = {
                            scope.launch {
                                bottomSheetState.hide()
                                showMediaPicker = false
                                val intent = Intent(context, PictureSelectorActivity::class.java)
                                videoPickerLauncher.launch(intent)
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            fontSize = 13.sp
        )
    }
}

@Composable
private fun MediaOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
        )
    }
}

@Composable
fun PostCircleScreenPreview() {
    AppTheme {
        PostCircleScreen()
    }
}
