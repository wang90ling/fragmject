package com.example.fragment.project

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.example.fragment.project.data.User
import com.example.fragment.project.ui.browse_history.BrowseHistoryScreen
import com.example.fragment.project.ui.circle.CircleListScreen
import com.example.fragment.project.ui.circle.ImagePreviewScreen
import com.example.fragment.project.ui.circle.PublishPostScreen
import com.example.fragment.project.ui.circle.VideoPlayerScreen
import com.example.fragment.project.ui.demo.DemoScreen
import com.example.fragment.project.ui.login.LoginNewScreen
import com.example.fragment.project.ui.main.MainScreen
import com.example.fragment.project.ui.my_coin.MyCoinScreen
import com.example.fragment.project.ui.my_collect.MyCollectScreen
import com.example.fragment.project.ui.my_share.MyShareScreen
import com.example.fragment.project.ui.rank.RankScreen
import com.example.fragment.project.ui.register.RegisterScreen
import com.example.fragment.project.ui.search.SearchScreen
import com.example.fragment.project.ui.setting.SettingScreen
import com.example.fragment.project.ui.share.ShareArticleScreen
import com.example.fragment.project.ui.system.SystemScreen
import com.example.fragment.project.ui.user.UserScreen
import com.example.fragment.project.ui.web.WebScreen
import com.example.fragment.project.utils.WanHelper
import com.example.miaow.base.vm.TRANSITION_TIME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    var user by remember { mutableStateOf<User?>(null) }
    var startDestination by remember { mutableStateOf<Any?>(null) }
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        val token = withContext(Dispatchers.IO) { WanHelper.getToken() }
        startDestination = if (token.isNullOrBlank()) LoginRoute else MainRoute
    }
    LaunchedEffect(Unit) { WanHelper.getUser().collect { user = it } }

    if (startDestination == null) return

    val wanNavActions = remember(navController, user) { WanNavActions(navController, user) }

    NavHost(
        navController = navController,
        startDestination = startDestination!!,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(TRANSITION_TIME))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(TRANSITION_TIME))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(TRANSITION_TIME))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(TRANSITION_TIME))
        },
    ) {
        composable<BrowseHistoryRoute> { BrowseHistoryScreen(onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<DemoRoute> { DemoScreen(onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<LoginRoute> { LoginNewScreen(onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }, onPopBackStack = { wanNavActions.popBackStack(it) }) }
        composable<MainRoute> { MainScreen(onNavigate = { wanNavActions.navigate(it) }) }
        composable<MyCoinRoute> { MyCoinScreen(onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<MyCollectRoute> { MyCollectScreen(onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<MyShareRoute> { MyShareScreen(onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<RankRoute>(deepLinks = listOf(navDeepLink<RankRoute>(basePath = "$fragmentUri/rank"))) { RankScreen(onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<RegisterRoute> { RegisterScreen(onNavigateUp = { wanNavActions.navigateUp() }, onPopBackStack = { wanNavActions.popBackStack(it) }) }
        composable<SearchRoute>(deepLinks = listOf(navDeepLink<SearchRoute>(basePath = "$fragmentUri/search"))) { backStackEntry -> SearchScreen(key = backStackEntry.toRoute<SearchRoute>().key, onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<SettingRoute> { SettingScreen(onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<ShareArticleRoute> { ShareArticleScreen(onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<SystemRoute> { backStackEntry -> SystemScreen(cid = backStackEntry.toRoute<SystemRoute>().cid, onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<UserRoute> { backStackEntry -> UserScreen(userId = backStackEntry.toRoute<UserRoute>().userId, onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<WebRoute>(deepLinks = listOf(navDeepLink<WebRoute>(basePath = "$fragmentUri/web"))) { backStackEntry -> WebScreen(url = backStackEntry.toRoute<WebRoute>().url, onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<CircleRoute> { CircleListScreen(onNavigate = { wanNavActions.navigate(it) }, onNavigateUp = { wanNavActions.navigateUp() }) }
        composable<CircleVideoRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<CircleVideoRoute>()
            VideoPlayerScreen(videoUrl = route.videoUrl, thumbnailUrl = route.thumbnailUrl, onNavigateUp = { wanNavActions.navigateUp() })
        }
        composable<CirclePublishRoute> {
            PublishPostScreen(onNavigateUp = { wanNavActions.navigateUp() })
        }
    }
}

const val fragmentUri = "wan://com.fragment.project"

class WanNavActions(private val navController: NavHostController, private val user: User?) {
    fun <T : Any> navigate(route: T) { navController.navigate(route) }
    fun navigateUp() { navController.navigateUp() }
    fun <T : Any> popBackStack(route: T) { navController.popBackStack(route, false) }
}

@Serializable object BrowseHistoryRoute
@Serializable object DemoRoute
@Serializable object LoginRoute
@Serializable object MainRoute
@Serializable object MyCoinRoute
@Serializable object MyCollectRoute
@Serializable object MyShareRoute
@Serializable object RankRoute
@Serializable object RegisterRoute
@Serializable data class SearchRoute(val key: String)
@Serializable object SettingRoute
@Serializable object ShareArticleRoute
@Serializable data class SystemRoute(val cid: String)
@Serializable data class UserRoute(val userId: String)
@Serializable data class WebRoute(val url: String)
@Serializable object DispatchCenterRoute
@Serializable object HotLiveRoomsRoute
@Serializable object CircleRoute
@Serializable object CirclePublishRoute
@Serializable data class CircleVideoRoute(val videoUrl: String, val thumbnailUrl: String = "")
@Serializable data class CircleUserRoute(val userId: String)
@Serializable data class CircleCommentRoute(val postId: String)
@Serializable data class CircleShareRoute(val postId: String)
