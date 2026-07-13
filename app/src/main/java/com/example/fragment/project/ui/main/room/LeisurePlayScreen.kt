package com.example.fragment.project.ui.main.room

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fragment.project.AppTheme

/**
 * 休闲玩占位页。
 *
 * 顶部"点Ta / 派单厅 / 树洞 / 休闲玩" Tab 已经在 [com.example.fragment.project.ui.main.home.HomeNewScreen] 中
 * 唯一驱动，本页只负责展示内容。
 */
@Composable
fun LeisurePlayScreen(
    onNavigate: (route: Any) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F4FF))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 36.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFB69CFF), Color(0xFF6C47FF))
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎮", fontSize = 36.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("休闲玩", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF191919))
                Spacer(modifier = Modifier.height(8.dp))
                Text("敬请期待", fontSize = 14.sp, color = Color(0xFF8E8E93))
            }
        }
    }
}
