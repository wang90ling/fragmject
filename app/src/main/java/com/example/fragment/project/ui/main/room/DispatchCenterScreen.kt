package com.example.fragment.project.ui.main.room

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
 * 派单厅内容页。
 *
 * 重要：这里不再包含顶部"点Ta / 派单厅 / 树洞 / 休闲玩" Tab，
 * 统一由 [com.example.fragment.project.ui.main.home.HomeNewScreen] 驱动，
 * 否则会出现两套 Tab 互相打架的 bug（点 Ta/派单厅 来回切不回去、动画叠加）。
 *
 * 视觉细节按截图：
 * - 顶部 3 列精选卡片（TOP 1-3），尺寸 1:1，左下角"派单"小标签 + 标题 + "暂无主持-" 副标题
 * - 下方单列横向卡片：左侧方形小图 + 右侧标题 + 副标题 + 右上角 ❤1100
 */
@Composable
fun DispatchCenterScreen(
    onNavigate: (route: Any) -> Unit = {},
) {
    val rooms = remember {
        listOf(
            DispatchRoom("test111", "暂无主持-", "1100", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600", true, 1),
            DispatchRoom("测试派单房", "暂无主持-", "1100", "https://images.unsplash.com/photo-1516972810927-80185027ca84?w=600", true, 2),
            DispatchRoom("厅 石伟", "暂无主持-", "1100", "https://images.unsplash.com/photo-1508672019048-805c876b67e2?w=600", true, 3),
            DispatchRoom("暂留test", "暂无主持-", "1100", "https://images.unsplash.com/photo-1500375592092-40eb2168fd21?w=600", false, 0),
            DispatchRoom("8468", "暂无主持-", "1100", "https://images.unsplash.com/photo-1548946502-76b3f5d5a4d8?w=600", false, 0),
            DispatchRoom("小浣熊派单厅", "暂无主持-", "1100", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=600", false, 0),
            DispatchRoom("派单厅", "暂无主持-", "1100", "https://images.unsplash.com/photo-1504593811423-6dd665756598?w=600", false, 0),
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F4FF)),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 顶部 3 列精选
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rooms.filter { it.isTop }.forEachIndexed { index, room ->
                    DispatchTopCard(
                        modifier = Modifier.weight(1f),
                        rank = room.rank,
                        title = room.title,
                        host = room.host
                    )
                    if (index == 0) Spacer(modifier = Modifier.width(0.dp))
                }
            }
        }

        // 单列横向卡片列表
        items(rooms.filter { !it.isTop }) { room ->
            DispatchListItemCard(
                title = room.title,
                host = room.host,
                coverUrl = room.coverUrl,
                heat = room.heat
            )
        }
    }
}

@Composable
private fun DispatchTopCard(modifier: Modifier = Modifier, rank: Int, title: String, host: String) {
    val brush = remember(rank) {
        when (rank) {
            1 -> Brush.verticalGradient(listOf(Color(0xFF9B86FF), Color(0xFF5A4BFF)))
            2 -> Brush.verticalGradient(listOf(Color(0xFFB9B0FF), Color(0xFF8A7CFF)))
            else -> Brush.verticalGradient(listOf(Color(0xFF322955), Color(0xFF1E1738)))
        }
    }
    Column(
        modifier = modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(14.dp))
            .background(brush)
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 顶部 TOP 标签
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF000000).copy(alpha = 0.4f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text("TOP $rank", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }

        // 底部标题区
        Column {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF000000).copy(alpha = 0.35f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("派单", color = Color.White, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = host,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DispatchListItemCard(title: String, host: String, coverUrl: String, heat: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧方形封面 + 派单角标
            Box(
                modifier = Modifier
                    .size(width = 70.dp, height = 60.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE0D7FF))
                )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF141414))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text("派单", color = Color.White, fontSize = 9.sp)
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color(0xFF191919),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = null,
                        tint = Color(0xFFB1A4F2),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = host,
                        color = Color(0xFF8E8E93),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            // 右上角 ❤ 1100
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("❤", color = Color(0xFFFF6A3D), fontSize = 13.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Text(heat, color = Color(0xFFFF6A3D), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private data class DispatchRoom(
    val title: String,
    val host: String,
    val heat: String,
    val coverUrl: String,
    val isTop: Boolean,
    val rank: Int,
)

@Preview(showBackground = true)
@Composable
fun DispatchCenterScreenPreview() {
    AppTheme { DispatchCenterScreen() }
}
