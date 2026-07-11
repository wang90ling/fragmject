package com.example.fragment.project.ui.main.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ControlCamera
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.fragment.project.AppTheme
import com.example.fragment.project.data.bean.response.CategoryItem
import com.example.fragment.project.data.bean.response.UserRecord

@Composable
fun HomeNewScreen(
    listState: LazyListState,
    viewModel: HomeViewModel = viewModel(),
    onNavigate: (route: Any) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F4FF)),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        item(key = "topBar") {
            HomeTopBar()
        }

        item(key = "hero") {
            HomeHeroSection()
        }

        itemsIndexed(
            items = uiState.categoryList ?: emptyList(),
            key = { index, item ->
                item.id.ifBlank { "category_$index" }
            }

        ) { _, item ->
            //HomeTalentCard(record = item)
            HomeCategoryTabs(category = item)
        }

        item(key = "filters") {
            HomeFilterRow()
        }

        itemsIndexed(
            items = uiState.homeRecommendResult.records ?: emptyList(),
            key = { index, item ->
                item.userId.ifBlank { "user_$index" }
            }

        ) { _, item ->
            HomeTalentCard(record = item)
        }
    }
}

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "点Ta",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF161616)
        )
        Spacer(modifier = Modifier.width(18.dp))
        Text(text = "派单厅", fontSize = 15.sp, color = Color(0xFF616161))
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = "树洞", fontSize = 15.sp, color = Color(0xFF616161))
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = "休闲玩", fontSize = 15.sp, color = Color(0xFF616161))
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = Color(0xFF191919))
        }
        IconButton(onClick = {}) {
            Icon(Icons.Filled.NotificationsNone, contentDescription = null, tint = Color(0xFF191919))
        }
    }
}

@Composable
private fun HomeHeroSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1.2f)
                .aspectRatio(1.15f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF58C8FF), Color(0xFF5B8DFF), Color(0xFFB064FF))
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "甜咸挑战",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "3月23日",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "甜咸大作战",
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier.weight(0.9f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HomeActionCard(
                title = "接待大厅",
                subtitle = "24小时陪伴",
                gradient = listOf(Color(0xFFFFFFFF), Color(0xFFF6F3FF)),
                accent = Color(0xFF9278FF)
            )
            HomeActionCard(
                title = "极速派单",
                subtitle = "一起打游戏",
                gradient = listOf(Color(0xFFFFFFFF), Color(0xFFF8FCFF)),
                accent = Color(0xFFFFC95C)
            )
        }
    }
}

@Composable
private fun HomeActionCard(
    title: String,
    subtitle: String,
    gradient: List<Color>,
    accent: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
                .padding(14.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF9B9B9B)
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ControlCamera,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeCategoryTabs(
    category: CategoryItem,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HomeTab(text = "推荐", selected = true)
        Spacer(modifier = Modifier.width(18.dp))
        HomeTab(text = "三角洲端游")
        Spacer(modifier = Modifier.width(18.dp))
        HomeTab(text = "三角洲-可下单")
        Spacer(modifier = Modifier.width(18.dp))
        HomeTab(text = "无畏契约")
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            tint = Color(0xFF9C9C9C),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun HomeTab(
    text: String,
    selected: Boolean = false,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color(0xFF111111) else Color(0xFF6F6F6F)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(if (selected) Color(0xFF7C6BFF) else Color.Transparent)
        )
    }
}

@Composable
private fun HomeFilterRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(text = "不限性别")
        Spacer(modifier = Modifier.width(10.dp))
        FilterChip(text = "不限塔子水平")
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "筛选",
            fontSize = 13.sp,
            color = Color(0xFF9B9B9B)
        )
    }
}

@Composable
private fun FilterChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF0EBFF))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF7A6DF6)
        )
    }
}

@Composable
private fun HomeTalentCard(
    record: UserRecord,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = record.avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF4F4F4))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.nickName.ifBlank { "未命名" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111111),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE8FFF0))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Lv.${record.level}",
                            fontSize = 11.sp,
                            color = Color(0xFF19A55A),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = record.categoryList?.firstOrNull()?.categoryName ?: "暂无分类",
                    fontSize = 12.sp,
                    color = Color(0xFF7A7A7A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.StarOutline,
                        contentDescription = null,
                        tint = Color(0xFFA58BFF),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${record.scoreAvg} 分",
                        fontSize = 12.sp,
                        color = Color(0xFF9A86FF)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (record.onlineFlag == 1) "在线" else "离线",
                    fontSize = 12.sp,
                    color = if (record.onlineFlag == 1) Color(0xFF18C57A) else Color(0xFF9B9B9B)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "${record.orderAmount} 单",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7A6DF6)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F4FF)
@Composable
fun HomeNewScreenPreview() {
    AppTheme {
        Surface(color = Color(0xFFF6F4FF)) {
            HomeNewScreen(rememberLazyListState())
        }
    }
}
