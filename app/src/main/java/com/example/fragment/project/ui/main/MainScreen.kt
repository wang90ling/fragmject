package com.example.fragment.project.ui.main

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.fragment.project.AppTheme
import com.example.fragment.project.R
import com.example.fragment.project.SearchRoute
import com.example.fragment.project.ShareArticleRoute
import com.example.fragment.project.WanViewModel
import com.example.fragment.project.components.LoopVerticalPager
import com.example.fragment.project.data.HotKey
import com.example.fragment.project.data.NavigationItem
import com.example.fragment.project.ui.main.home.HomeNewScreen
import com.example.fragment.project.ui.main.my.MimeScreen
import com.example.fragment.project.ui.main.nav.NavScreen
import com.example.fragment.project.ui.main.project.ProjectScreen
import kotlinx.coroutines.launch
import androidx.compose.ui.res.colorResource

@Composable
fun MainScreen(
    viewModel: WanViewModel = viewModel(),
    onNavigate: (route: Any) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val homeListState = rememberLazyListState()
    var navIndex by rememberSaveable { mutableIntStateOf(0) }
    val navItems = remember {
        listOf(
            NavigationItem("首页", R.drawable.tab_home_normal),
            NavigationItem("圈子", R.drawable.tab_circle_normal),
            NavigationItem("消息", R.drawable.tab_chat_normal),
            NavigationItem("我的", R.drawable.tab_me_normal),
        )
    }
    Scaffold(
        /*topBar = {
            SearchBar(
                data = uiState.hotKeyResult,
                onNavigate = onNavigate,
            )
        },*/
        bottomBar = {
            FloatingBottomNavigation(
                items = navItems,
                selectedIndex = navIndex,
            ) {
                if ((it == 0) && (navIndex == 0) && homeListState.canScrollBackward) {
                    scope.launch { homeListState.animateScrollToItem(0) }
                }
                navIndex = it
            }
        }
    ) { innerPadding ->
        val saveableStateHolder = rememberSaveableStateHolder()
        Column(modifier = Modifier.padding(innerPadding)) {
            when (navIndex) {
                0 -> saveableStateHolder.SaveableStateProvider(navItems[0].label) {
                    HomeNewScreen(
                        listState = homeListState,
                        onNavigate = onNavigate,
                    )
                }
                1 -> saveableStateHolder.SaveableStateProvider(navItems[1].label) {
                    NavScreen(
                        systemData = uiState.treeResult,
                        onNavigate = onNavigate,
                    )
                }
                2 -> saveableStateHolder.SaveableStateProvider(navItems[2].label) {
                    ProjectScreen(onNavigate = onNavigate)
                }
                3 -> saveableStateHolder.SaveableStateProvider(navItems[3].label) {
                    MimeScreen(onNavigate = onNavigate)
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    data: List<HotKey>?,
    onNavigate: (route: Any) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .statusBarsPadding()
            .fillMaxWidth()
            .height(42.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(15.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clipToBounds()
                .background(AppTheme.alphaGray)
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.padding(10.dp, 5.dp, 0.dp, 5.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            LoopVerticalPager(data = data) { _, _, item ->
                Box(
                    modifier = Modifier
                        .clickable { onNavigate(SearchRoute(item.name)) }
                        .fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = item.name,
                        modifier = Modifier.padding(start = 40.dp),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
        IconButton(
            modifier = Modifier.height(45.dp),
            onClick = { onNavigate(ShareArticleRoute) }
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun FloatingBottomNavigation(
    items: List<NavigationItem> = listOf(),
    selectedIndex: Int = 0,
    onClick: (index: Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .navigationBarsPadding(),
            //.padding(horizontal = 16.dp, vertical = 10.dp),
            //.shadow(18.dp, RoundedCornerShape(28.dp)),
        //shape = RoundedCornerShape(5.dp),
        color = colorResource(R.color.color_70_FFFFFF),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val selected = selectedIndex == index
                Column(
                    modifier = Modifier
                        .weight(1f)
                        //.clip(RoundedCornerShape(20.dp))
                        //.background(if (selected) Color(0xFFF2EDFF) else Color.Transparent)
                        .clickable { onClick(index) },
                        //.padding(vertical = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = item.resId),
                        contentDescription = null,
                        modifier = Modifier
                            .width(30.dp)
                            .height(30.dp)
                    )

                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        color = if (selected) Color(0xFF222222) else Color(0xFF9B9B9B)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun MainScreenPreview() {
    AppTheme { MainScreen() }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun SearchBarPreview() {
    AppTheme { SearchBar(data = listOf(HotKey(name = "问答"))) }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun WanBottomNavigationPreview() {
    val navItems = listOf(
        NavigationItem("首页", R.mipmap.ic_bottom_bar_home),
        NavigationItem("圈子", R.mipmap.ic_bottom_bar_navigation),
        NavigationItem("消息", R.mipmap.ic_bottom_bar_project),
        NavigationItem("我的", R.mipmap.ic_bottom_bar_user),
    )
    AppTheme { FloatingBottomNavigation(items = navItems, selectedIndex = 0, onClick = { _ -> }) }
}
