package com.example.fragment.project.ui.main.my

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.fragment.project.AppTheme
import com.example.fragment.project.BrowseHistoryRoute
import com.example.fragment.project.DemoRoute
import com.example.fragment.project.LoginRoute
import com.example.fragment.project.MyCoinRoute
import com.example.fragment.project.MyCollectRoute
import com.example.fragment.project.MyShareRoute
import com.example.fragment.project.SettingRoute
import com.example.fragment.project.UserRoute
import com.example.fragment.project.components.ArrowRightItem
import com.example.fragment.project.data.User

@Composable
fun MimeScreen(
    viewModel: MyViewModel = viewModel(),
    onNavigate: (route: Any) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF6F3F8)) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                MyHeader(
                    user = uiState.user,
                    onAvatarClick = {
                        if (uiState.user.id > 0) {
                            onNavigate(UserRoute(uiState.user.id.toString()))
                        } else {
                            onNavigate(LoginRoute)
                        }
                    },
                    onProfileClick = {
                        if (uiState.user.id > 0) {
                            onNavigate(UserRoute(uiState.user.id.toString()))
                        } else {
                            onNavigate(LoginRoute)
                        }
                    }
                )

                MyQuickEntryPanel(
                    onOrderClick = { onNavigate(MyCoinRoute) },
                    onCouponClick = { onNavigate(MyCollectRoute) },
                    onWalletClick = { onNavigate(MyShareRoute) }
                )

                MySectionCard(title = "更多服务") {
                    ServiceGrid(
                        items = listOf(
                            ServiceItem("装扮中心", Icons.Filled.Checkroom, Color(0xFF6A58FF)),
                            ServiceItem("塔子入驻", Icons.Filled.StarBorder, Color(0xFFD93A9D)),
                            ServiceItem("我的房间", Icons.Filled.Storefront, Color(0xFFEF6B8D)),
                            ServiceItem("贵族特权", Icons.Filled.CardGiftcard, Color(0xFFF0A02F)),
                        )
                    )
                }

                MySectionCard(title = "支持与帮助") {
                    HelpGrid(
                        items = listOf(
                            HelpItem("联系客服", Icons.Filled.SupportAgent, onClick = { onNavigate(DemoRoute) }),
                            HelpItem("举报记录", Icons.Filled.ReceiptLong, onClick = { onNavigate(BrowseHistoryRoute) }),
                            HelpItem("帮助中心", Icons.Filled.HelpOutline, onClick = { onNavigate(DemoRoute) }),
                            HelpItem("设置", Icons.Filled.Settings, onClick = { onNavigate(SettingRoute) }),
                            HelpItem("专属客服", Icons.Filled.Face, onClick = { onNavigate(DemoRoute) }),
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }

        }
    }
}

@Composable
private fun MyHeader(
    user: User,
    onAvatarClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF7D7FF), Color(0xFFF8EDF9), Color(0xFFF6F3F8))
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.avatar,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onAvatarClick)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.nickname?.takeIf { it.isNotBlank() }
                            ?: user.username?.takeIf { it.isNotBlank() }
                            ?: "热血星芒使YC...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF181818),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onProfileClick, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFFB089AC)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ID:${user.id}",
                        fontSize = 13.sp,
                        color = Color(0xFF777777)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF43C38B))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Lv.2",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "个人主页",
                fontSize = 13.sp,
                color = Color(0xFF8F718E)
            )
            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF8F718E),
                modifier = Modifier.size(12.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 78.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatBlock(number = "2", label = "关注")
            StatBlock(number = "1", label = "粉丝")
        }
    }
}

@Composable
private fun StatBlock(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = number, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D2D2D))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 13.sp, color = Color(0xFF7A6E79))
    }
}

@Composable
private fun MyQuickEntryPanel(
    onOrderClick: () -> Unit,
    onCouponClick: () -> Unit,
    onWalletClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuickActionItem(
                title = "订单",
                icon = Icons.Filled.ReceiptLong,
                tint = Color(0xFFF36D9D),
                onClick = onOrderClick
            )
            QuickActionItem(
                title = "优惠券",
                icon = Icons.Filled.CardGiftcard,
                tint = Color(0xFF5F7BFF),
                onClick = onCouponClick
            )
            QuickActionItem(
                title = "钱包",
                icon = Icons.Filled.LocalAtm,
                tint = Color(0xFFB44BFF),
                onClick = onWalletClick
            )
        }
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, fontSize = 14.sp, color = Color(0xFF2B2B2B))
    }
}

@Composable
private fun MySectionCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(start = 16.dp, top = 12.dp, end = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2B2B)
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun ServiceGrid(items: List<ServiceItem>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items.forEach { item ->
            ServiceGridItem(item = item)
        }
    }
}

@Composable
private fun ServiceGridItem(item: ServiceItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = item.tint,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = item.title,
            fontSize = 13.sp,
            color = Color(0xFF3A3A3A),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HelpGrid(items: List<HelpItem>) {
    val rows = items.chunked(4)
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { item ->
                    HelpGridItem(item = item)
                }
                repeat(4 - row.size) {
                    Spacer(modifier = Modifier.width(72.dp))
                }
            }
        }
    }
}

@Composable
private fun HelpGridItem(item: HelpItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = item.onClick)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = Color(0xFF5A5A5A),
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = item.title,
            fontSize = 13.sp,
            color = Color(0xFF3A3A3A),
            textAlign = TextAlign.Center
        )
    }
}

private data class ServiceItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tint: Color,
)

private data class HelpItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit,
)

@Preview(showBackground = true, backgroundColor = 0xFFF6F3F8)
@Composable
fun MimeScreenPreview() {
    AppTheme {
        MimeScreen()
    }
}
