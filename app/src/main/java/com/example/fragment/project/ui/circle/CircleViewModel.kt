package com.example.fragment.project.ui.circle

import androidx.lifecycle.viewModelScope
import com.example.fragment.project.data.circle.Circle
import com.example.fragment.project.data.circle.CircleComment
import com.example.miaow.base.vm.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * 圈子动态列表状态
 */
data class CircleListUiState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val isFinishing: Boolean = false,
    val result: List<Circle> = emptyList(),
)

/**
 * 帖子详情状态
 */
data class CircleDetailUiState(
    val circle: Circle? = null,
    val comments: List<CircleComment> = emptyList(),
    val isLoading: Boolean = false,
    val commentText: String = "",
)

/**
 * 发布帖子状态
 */
data class PostCircleUiState(
    val content: String = "",
    val images: List<String> = emptyList(),
    val videoPath: String? = null,
    val isPosting: Boolean = false,
    val error: String? = null,
)

class CircleViewModel : BaseViewModel() {

    private val _listState = MutableStateFlow(CircleListUiState())
    val listState: StateFlow<CircleListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(CircleDetailUiState())
    val detailState: StateFlow<CircleDetailUiState> = _detailState.asStateFlow()

    private val _postState = MutableStateFlow(PostCircleUiState())
    val postState: StateFlow<PostCircleUiState> = _postState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        viewModelScope.launch {
            val mockCircles = generateMockCircles()
            _listState.update {
                it.copy(result = mockCircles, isRefreshing = false, isFinishing = true)
            }
        }
    }

    fun refresh() {
        _listState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            val mockCircles = generateMockCircles()
            _listState.update {
                it.copy(result = mockCircles, isRefreshing = false, isFinishing = true)
            }
        }
    }

    fun loadMore() {
        if (_listState.value.isLoading || _listState.value.isFinishing) return
        _listState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(500)
            val newCircles = generateMoreMockCircles(_listState.value.result.size)
            _listState.update {
                it.copy(
                    result = it.result + newCircles,
                    isLoading = false,
                    isFinishing = true
                )
            }
        }
    }

    fun likeCircle(circleId: String) {
        _listState.update { state ->
            state.copy(
                result = state.result.map { circle ->
                    if (circle.id == circleId) {
                        circle.copy(
                            isLiked = !circle.isLiked,
                            likeCount = if (circle.isLiked) circle.likeCount - 1 else circle.likeCount + 1
                        )
                    } else circle
                }
            )
        }
    }

    fun shareCircle(circleId: String) {
        _listState.update { state ->
            state.copy(
                result = state.result.map { circle ->
                    if (circle.id == circleId) {
                        circle.copy(shareCount = circle.shareCount + 1)
                    } else circle
                }
            )
        }
    }

    fun selectCircle(circle: Circle) {
        _detailState.update { it.copy(circle = circle, comments = generateMockComments(circle.id)) }
    }

    fun updateCommentText(text: String) {
        _detailState.update { it.copy(commentText = text) }
    }

    fun postComment() {
        val circle = _detailState.value.circle ?: return
        val commentText = _detailState.value.commentText
        if (commentText.isBlank()) return

        val newComment = CircleComment(
            id = UUID.randomUUID().toString(),
            circleId = circle.id,
            userId = "current_user",
            userName = "当前用户",
            userAvatar = "",
            content = commentText,
            createTime = System.currentTimeMillis()
        )

        _detailState.update { state ->
            state.copy(
                comments = state.comments + newComment,
                commentText = "",
                circle = circle.copy(commentCount = circle.commentCount + 1)
            )
        }

        _listState.update { state ->
            state.copy(
                result = state.result.map { c ->
                    if (c.id == circle.id) c.copy(commentCount = c.commentCount + 1) else c
                }
            )
        }
    }

    fun updatePostContent(content: String) {
        _postState.update { it.copy(content = content) }
    }

    fun addPostImage(imagePath: String) {
        _postState.update { state ->
            if (state.images.size < 9) {
                state.copy(images = state.images + imagePath)
            } else state
        }
    }

    fun removePostImage(imagePath: String) {
        _postState.update { state ->
            state.copy(images = state.images - imagePath)
        }
    }

    fun movePostImage(from: Int, to: Int) {
        if (from == to) return
        _postState.update { state ->
            val mutableImages = state.images.toMutableList()
            val item = mutableImages.removeAt(from)
            mutableImages.add(to, item)
            state.copy(images = mutableImages)
        }
    }

    fun setPostVideo(videoPath: String?) {
        _postState.update { it.copy(videoPath = videoPath) }
    }

    fun postCircle() {
        val state = _postState.value
        if (state.content.isBlank() && state.images.isEmpty() && state.videoPath == null) {
            _postState.update { it.copy(error = "内容不能为空") }
            return
        }

        _postState.update { it.copy(isPosting = true, error = null) }

        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)

            val newCircle = Circle(
                id = UUID.randomUUID().toString(),
                userId = "current_user",
                userName = "当前用户",
                userAvatar = "",
                content = state.content,
                images = state.images,
                video = state.videoPath,
                createTime = System.currentTimeMillis()
            )

            _listState.update { it.copy(result = listOf(newCircle) + it.result) }
            _postState.update { PostCircleUiState() }
        }
    }

    fun resetPostState() {
        _postState.update { PostCircleUiState() }
    }

    fun clearDetail() {
        _detailState.update { CircleDetailUiState() }
    }

    private fun generateMockCircles(): List<Circle> {
        val now = System.currentTimeMillis()
        return listOf(
            Circle(
                id = "1",
                userId = "user1",
                userName = "小明",
                userAvatar = "",
                content = "今天天气真好！出去散步拍了一些照片，分享给大家～",
                images = listOf(
                    "https://picsum.photos/800/600?random=1",
                    "https://picsum.photos/800/600?random=2",
                    "https://picsum.photos/800/600?random=3"
                ),
                createTime = now - 5 * 60 * 1000,
                likeCount = 128,
                commentCount = 23,
                shareCount = 5,
                isLiked = false
            ),
            Circle(
                id = "2",
                userId = "user2",
                userName = "小红",
                userAvatar = "",
                content = "记录一下今天的学习成果，掌握了新技能！",
                images = listOf(
                    "https://picsum.photos/800/600?random=4"
                ),
                createTime = now - 30 * 60 * 1000,
                likeCount = 256,
                commentCount = 45,
                shareCount = 12,
                isLiked = true
            ),
            Circle(
                id = "3",
                userId = "user3",
                userName = "小刚",
                userAvatar = "",
                content = "周末自驾游拍的风景，太美了！",
                images = listOf(
                    "https://picsum.photos/800/600?random=5",
                    "https://picsum.photos/800/600?random=6"
                ),
                createTime = now - 2 * 60 * 60 * 1000,
                likeCount = 89,
                commentCount = 15,
                shareCount = 8,
                isLiked = false
            ),
            Circle(
                id = "4",
                userId = "user4",
                userName = "小美",
                userAvatar = "",
                content = "美食推荐！这家店的招牌菜太好吃了",
                images = listOf(
                    "https://picsum.photos/800/600?random=7",
                    "https://picsum.photos/800/600?random=8",
                    "https://picsum.photos/800/600?random=9",
                    "https://picsum.photos/800/600?random=10"
                ),
                createTime = now - 5 * 60 * 60 * 1000,
                likeCount = 312,
                commentCount = 67,
                shareCount = 23,
                isLiked = false
            ),
            Circle(
                id = "5",
                userId = "user5",
                userName = "小华",
                userAvatar = "",
                content = "拍了一段延时摄影，记录城市的日夜变化",
                images = listOf(
                    "https://picsum.photos/800/600?random=11",
                    "https://picsum.photos/800/600?random=12",
                    "https://picsum.photos/800/600?random=13",
                    "https://picsum.photos/800/600?random=14",
                    "https://picsum.photos/800/600?random=15",
                    "https://picsum.photos/800/600?random=16"
                ),
                createTime = now - 1 * 24 * 60 * 60 * 1000,
                likeCount = 567,
                commentCount = 89,
                shareCount = 45,
                isLiked = true
            ),
            Circle(
                id = "6",
                userId = "user6",
                userName = "小丽",
                userAvatar = "",
                content = "今天拍了好多照片，选了9张最满意的分享给大家",
                images = listOf(
                    "https://picsum.photos/800/600?random=17",
                    "https://picsum.photos/800/600?random=18",
                    "https://picsum.photos/800/600?random=19",
                    "https://picsum.photos/800/600?random=20",
                    "https://picsum.photos/800/600?random=21",
                    "https://picsum.photos/800/600?random=22",
                    "https://picsum.photos/800/600?random=23",
                    "https://picsum.photos/800/600?random=24",
                    "https://picsum.photos/800/600?random=25"
                ),
                createTime = now - 2 * 24 * 60 * 60 * 1000,
                likeCount = 1024,
                commentCount = 156,
                shareCount = 78,
                isLiked = false
            ),
            Circle(
                id = "7",
                userId = "user7",
                userName = "小杰",
                userAvatar = "",
                content = "纯文字动态，没有什么特别的，就是想发个状态",
                images = emptyList(),
                createTime = now - 3 * 24 * 60 * 60 * 1000,
                likeCount = 12,
                commentCount = 3,
                shareCount = 1,
                isLiked = false
            ),
            Circle(
                id = "8",
                userId = "user8",
                userName = "小芳",
                userAvatar = "",
                content = "整理照片时翻到去年这个时候拍的，很怀念",
                images = listOf(
                    "https://picsum.photos/800/600?random=26",
                    "https://picsum.photos/800/600?random=27",
                    "https://picsum.photos/800/600?random=28",
                    "https://picsum.photos/800/600?random=29",
                    "https://picsum.photos/800/600?random=30",
                    "https://picsum.photos/800/600?random=31"
                ),
                createTime = now - 15 * 24 * 60 * 60 * 1000,
                likeCount = 234,
                commentCount = 34,
                shareCount = 15,
                isLiked = true
            ),
            Circle(
                id = "9",
                userId = "user9",
                userName = "小强",
                userAvatar = "",
                content = "周末钓鱼的收获，大丰收！",
                images = listOf(
                    "https://picsum.photos/800/600?random=32",
                    "https://picsum.photos/800/600?random=33",
                    "https://picsum.photos/800/600?random=34"
                ),
                createTime = now - 45 * 24 * 60 * 60 * 1000,
                likeCount = 445,
                commentCount = 56,
                shareCount = 28,
                isLiked = false
            ),
            Circle(
                id = "10",
                userId = "user10",
                userName = "小雪",
                userAvatar = "",
                content = "几个月前的旅行照片，现在看还是觉得很美",
                images = listOf(
                    "https://picsum.photos/800/600?random=35",
                    "https://picsum.photos/800/600?random=36"
                ),
                createTime = now - 120 * 24 * 60 * 60 * 1000,
                likeCount = 678,
                commentCount = 89,
                shareCount = 45,
                isLiked = false
            )
        )
    }

    private fun generateMoreMockCircles(offset: Int): List<Circle> {
        val now = System.currentTimeMillis()
        return listOf(
            Circle(
                id = "${offset + 1}",
                userId = "user${offset + 1}",
                userName = "新用户${offset + 1}",
                userAvatar = "",
                content = "这是第${offset + 1}条新加载的动态内容",
                images = listOf(
                    "https://picsum.photos/800/600?random=${100 + offset}",
                    "https://picsum.photos/800/600?random=${101 + offset}"
                ),
                createTime = now - (offset + 5) * 24 * 60 * 60 * 1000L,
                likeCount = (offset + 1) * 10,
                commentCount = (offset + 1) * 2,
                shareCount = offset,
                isLiked = false
            ),
            Circle(
                id = "${offset + 2}",
                userId = "user${offset + 2}",
                userName = "新用户${offset + 2}",
                userAvatar = "",
                content = "又一条新动态，分享给大家",
                images = listOf(
                    "https://picsum.photos/800/600?random=${102 + offset}",
                    "https://picsum.photos/800/600?random=${103 + offset}",
                    "https://picsum.photos/800/600?random=${104 + offset}",
                    "https://picsum.photos/800/600?random=${105 + offset}"
                ),
                createTime = now - (offset + 10) * 24 * 60 * 60 * 1000L,
                likeCount = (offset + 2) * 15,
                commentCount = (offset + 2) * 3,
                shareCount = offset + 1,
                isLiked = offset % 2 == 0
            )
        )
    }

    private fun generateMockComments(circleId: String): List<CircleComment> {
        val now = System.currentTimeMillis()
        return listOf(
            CircleComment(
                id = "c1",
                circleId = circleId,
                userId = "commenter1",
                userName = "评论者1",
                userAvatar = "",
                content = "写得真好，支持一下！",
                createTime = now - 10 * 60 * 1000,
                replyCount = 3
            ),
            CircleComment(
                id = "c2",
                circleId = circleId,
                userId = "commenter2",
                userName = "评论者2",
                userAvatar = "",
                content = "这个观点很有意思，点赞！",
                createTime = now - 30 * 60 * 1000,
                replyCount = 1
            ),
            CircleComment(
                id = "c3",
                circleId = circleId,
                userId = "commenter3",
                userName = "评论者3",
                userAvatar = "",
                content = "同感同感，我也遇到过类似的情况",
                createTime = now - 2 * 60 * 60 * 1000,
                replyCount = 5
            )
        )
    }
}
