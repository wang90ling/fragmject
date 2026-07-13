package com.example.fragment.project.ui.main.room

import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fragment.project.AppTheme

/**
 * 树洞内容页。
 *
 * 重要：顶部"点Ta / 派单厅 / 树洞 / 休闲玩" Tab 已经在 [com.example.fragment.project.ui.main.home.HomeNewScreen] 中
 * 唯一驱动，本页只负责次级 Tab（热门 / 小圈 / 点唱 / 情感 / 交友 / 电台）和内容区。
 *
 * 视觉按截图：
 * - 次级 Tab 是一排胶囊，选中态使用淡紫色背景 + 紫色描边 + 紫色粗体文字
 * - 内容区是 2 列网格卡片，卡片上方有彩色 tag（"交友"/"点唱"/"电台"），右上角有热度（如 1.4w）
 * - 卡片底部白色"暂无主持~"+ 大字标题
 */
@Composable
fun HotLiveRoomsScreen(
    onNavigate: (route: Any) -> Unit = {},
) {
    val tabs = remember {
        listOf("热门", "小圈", "点唱", "情感", "交友", "电台")
    }
    var selectedTab by rememberSaveable { mutableStateOf(tabs.first()) }
    val rooms = remember(selectedTab) { buildHotRooms(selectedTab) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F4FF))
    ) {
        // 次级 Tab
        HotTabs(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // 2 列网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
        ) {
            items(rooms) { room ->
                HotRoomCard(room = room)
            }
        }
    }
}

@Composable
private fun HotTabs(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { tab ->
            val selected = tab == selectedTab
            // 切换 Tab 时背景 / 文字色 / 边框色都用 animateColorAsState 缓动，避免硬切
            val bgColor by animateColorAsState(
                targetValue = if (selected) Color(0xFFEEE4FF) else Color.White,
                animationSpec = tween(220),
                label = "tab_bg"
            )
            val borderColor by animateColorAsState(
                targetValue = if (selected) Color(0xFFB69CFF) else Color(0xFFEEE6F7),
                animationSpec = tween(220),
                label = "tab_border"
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) Color(0xFF6C47FF) else Color(0xFF8F8396),
                animationSpec = tween(220),
                label = "tab_text"
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(999.dp))
                    .clickable(onClick = { onTabSelected(tab) })
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = tab,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun HotRoomCard(room: HotRoom) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.92f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = room.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0D7FF))
            )
            // 底部暗色渐变，让白色文字可读
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.05f),
                                Color.Black.copy(alpha = 0.6f)
                            )
                        )
                    )
            )
            // 左上角 tag
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(room.tagColor.copy(alpha = 0.92f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(room.tag, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            // 右上角热度
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFFF4EEF8).copy(alpha = 0.95f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(room.heat, color = Color(0xFF8B6CFF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            // 加密房间加锁图标
            if (room.locked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
            // 底部 host + title
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = room.host,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = room.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private data class HotRoom(
    val title: String,
    val host: String,
    val heat: String,
    val coverUrl: String,
    val tag: String,
    val tagColor: Color,
    val locked: Boolean = false,
)

private fun buildHotRooms(selectedTab: String): List<HotRoom> {
    return when (selectedTab) {
        "交友" -> listOf(
            HotRoom("Pink公馆10麦房", "暂无主持-", "1.4w", "https://images.unsplash.com/photo-1521334884684-d80222895322?w=800", "交友", Color(0xFFFF5DB1)),
            HotRoom("测试3-6 的 通用十麦房", "暂无主持-", "1100", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800", "点唱", Color(0xFF9B6BFF)),
            HotRoom("测试4-0 适用十麦房", "暂无主持-", "1100", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=800", "点唱", Color(0xFFD65DFF)),
            HotRoom("测试5-1 的通用十麦房", "暂无主持-", "1100", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=800", "交友", Color(0xFFFF5D7A)),
            HotRoom("小浣熊010", "暂无主持-", "2.11w", "https://images.unsplash.com/photo-1518020382113-a7e8fc38eac9?w=800", "点唱", Color(0xFF7A66FF)),
            HotRoom("头牌电台", "暂无主持-", "1.51w", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=800", "电台", Color(0xFF4E9BFF), locked = true),
        )
        else -> listOf(
            HotRoom("Pink公馆10麦房", "暂无主持-", "1.4w", "https://images.unsplash.com/photo-1521334884684-d80222895322?w=800", "交友", Color(0xFFFF5DB1)),
            HotRoom("测试3-6 的 通用十麦房", "暂无主持-", "1100", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800", "点唱", Color(0xFF9B6BFF)),
            HotRoom("测试4-0 适用十麦房", "暂无主持-", "1100", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=800", "点唱", Color(0xFFD65DFF)),
            HotRoom("测试5-1 的通用十麦房", "暂无主持-", "1100", "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=800", "交友", Color(0xFFFF5D7A)),
            HotRoom("小浣熊010", "暂无主持-", "2.11w", "https://images.unsplash.com/photo-1518020382113-a7e8fc38eac9?w=800", "点唱", Color(0xFF7A66FF)),
            HotRoom("头牌电台", "暂无主持-", "1.51w", "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=800", "电台", Color(0xFF4E9BFF), locked = true),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HotLiveRoomsScreenPreview() {
    AppTheme { HotLiveRoomsScreen() }
}
