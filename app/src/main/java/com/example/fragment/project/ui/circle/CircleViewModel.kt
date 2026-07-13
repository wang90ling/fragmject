package com.example.fragment.project.ui.circle

import androidx.lifecycle.viewModelScope
import com.example.fragment.project.data.circle.CirclePost
import com.example.fragment.project.data.circle.MediaItem
import com.example.fragment.project.data.circle.MediaType
import com.example.miaow.base.vm.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CircleUiState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isFinishing: Boolean = false,
    val result: List<CirclePost> = emptyList(),
    val error: String? = null
)

class CircleViewModel : BaseViewModel() {

    private val _uiState = MutableStateFlow(CircleUiState())
    val uiState: StateFlow<CircleUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isHomePage = true

    init {
        getHome()
    }

    fun getHome() {
        isHomePage = true
        currentPage = 1
        _uiState.update {
            it.copy(isRefreshing = true, isLoading = false, isFinishing = false, error = null)
        }
        loadPosts(currentPage)
    }

    fun getNext() {
        if (_uiState.value.isLoading || _uiState.value.isFinishing) return
        _uiState.update {
            it.copy(isRefreshing = false, isLoading = true, isFinishing = false)
        }
        loadPosts(currentPage + 1)
    }

    private fun loadPosts(page: Int) {
        viewModelScope.launch {
            try {
                delay(500)

                val mockPosts = generateMockPosts(page)
                currentPage = page

                _uiState.update { state ->
                    val merged = if (isHomePage || page == 1) {
                        mockPosts
                    } else {
                        state.result + mockPosts
                    }
                    state.copy(
                        isRefreshing = false,
                        isLoading = false,
                        isFinishing = page >= 3,
                        result = merged,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        isLoading = false,
                        error = e.message ?: "加载失败"
                    )
                }
            }
        }
    }

    fun likePost(postId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    result = state.result.map { post ->
                        if (post.id == postId) {
                            post.copy(
                                isLiked = !post.isLiked,
                                likeCount = if (post.isLiked) post.likeCount - 1 else post.likeCount + 1
                            )
                        } else post
                    }
                )
            }
        }
    }

    fun publishPost(content: String, mediaList: List<MediaItem>) {
        viewModelScope.launch {
            try {
                val newPost = CirclePost(
                    id = System.currentTimeMillis().toString(),
                    userId = "current_user",
                    userName = "当前用户",
                    content = content,
                    mediaList = mediaList,
                    createTime = System.currentTimeMillis().toString(),
                    likeCount = 0,
                    commentCount = 0,
                    shareCount = 0,
                    isLiked = false
                )
                _uiState.update { state ->
                    state.copy(result = listOf(newPost) + state.result)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "发布失败: ${e.message}")
                }
            }
        }
    }

    private fun generateMockPosts(page: Int): List<CirclePost> {
        val baseTime = System.currentTimeMillis()
        return listOf(
            CirclePost(
                id = "${page}_1",
                userId = "1",
                userName = "小明",
                userAvatar = "",
                content = "今天天气真好，出去散步拍了几张照片，感受到了春天的气息。🌸",
                mediaList = listOf(
                    MediaItem(url = "https://picsum.photos/800/600?random=1", type = MediaType.IMAGE),
                    MediaItem(url = "https://picsum.photos/800/600?random=2", type = MediaType.IMAGE),
                    MediaItem(url = "https://picsum.photos/800/600?random=3", type = MediaType.IMAGE)
                ),
                createTime = (baseTime - 5 * 60 * 1000).toString(),
                likeCount = 128,
                commentCount = 23,
                shareCount = 5,
                isLiked = false,
                topicName = "日常"
            ),
            CirclePost(
                id = "${page}_2",
                userId = "2",
                userName = "摄影爱好者",
                userAvatar = "",
                content = "分享一组最近拍的城市夜景，灯火通明，车水马龙。",
                mediaList = listOf(
                    MediaItem(url = "https://picsum.photos/800/1200?random=4", type = MediaType.IMAGE)
                ),
                createTime = (baseTime - 30 * 60 * 1000).toString(),
                likeCount = 256,
                commentCount = 45,
                shareCount = 12,
                isLiked = true,
                topicName = "摄影"
            ),
            CirclePost(
                id = "${page}_3",
                userId = "3",
                userName = "美食达人",
                userAvatar = "",
                content = "周末自己做了一顿大餐，犒劳一下自己！厨艺又进步了~",
                mediaList = listOf(
                    MediaItem(url = "https://picsum.photos/800/800?random=5", type = MediaType.IMAGE),
                    MediaItem(url = "https://picsum.photos/800/800?random=6", type = MediaType.IMAGE)
                ),
                createTime = (baseTime - 2 * 60 * 60 * 1000).toString(),
                likeCount = 89,
                commentCount = 15,
                shareCount = 3,
                isLiked = false,
                topicName = "美食"
            ),
            CirclePost(
                id = "${page}_4",
                userId = "4",
                userName = "旅行家",
                userAvatar = "",
                content = "云南之旅 Day3 | 丽江古城的小巷子，每一步都是风景。",
                mediaList = (1..6).map {
                    MediaItem(url = "https://picsum.photos/800/800?random=${7 + it}", type = MediaType.IMAGE)
                },
                createTime = (baseTime - 5 * 60 * 60 * 1000).toString(),
                likeCount = 512,
                commentCount = 78,
                shareCount = 25,
                isLiked = false,
                topicName = "旅行"
            ),
            CirclePost(
                id = "${page}_5",
                userId = "5",
                userName = "健身教练",
                userAvatar = "",
                content = "坚持健身第100天打卡！分享一下我的训练计划~",
                mediaList = listOf(
                    MediaItem(url = "https://picsum.photos/800/1000?random=14", type = MediaType.IMAGE)
                ),
                createTime = (baseTime - 1 * 24 * 60 * 60 * 1000).toString(),
                likeCount = 1024,
                commentCount = 156,
                shareCount = 45,
                isLiked = true,
                topicName = "健身"
            ),
            CirclePost(
                id = "${page}_6",
                userId = "6",
                userName = "铲屎官",
                userAvatar = "",
                content = "我家猫咪今天学会新技能了，居然会自己开门！太聪明了哈哈哈",
                mediaList = (1..9).map {
                    MediaItem(url = "https://picsum.photos/800/800?random=${20 + it}", type = MediaType.IMAGE)
                },
                createTime = (baseTime - 2 * 24 * 60 * 60 * 1000).toString(),
                likeCount = 2048,
                commentCount = 312,
                shareCount = 89,
                isLiked = false,
                topicName = "日常"
            )
        )
    }
}
