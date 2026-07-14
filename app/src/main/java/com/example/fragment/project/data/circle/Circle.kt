package com.example.fragment.project.data.circle

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 圈子动态数据模型
 */
@Parcelize
data class Circle(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val content: String = "",
    val images: List<String> = emptyList(),
    val video: String? = null,
    val videoThumbnail: String? = null,
    val createTime: Long = 0L,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val isLiked: Boolean = false,
    val location: String? = null,
) : Parcelable {

    val hasVideo: Boolean get() = !video.isNullOrBlank()
    val hasImages: Boolean get() = images.isNotEmpty()
    val mediaCount: Int get() = if (hasVideo) images.size + 1 else images.size

    val isSingleImage: Boolean get() = images.size == 1 && !hasVideo
    val isTwoImages: Boolean get() = images.size == 2 && !hasVideo
    val isThreeImages: Boolean get() = images.size == 3 && !hasVideo
    val isFourImages: Boolean get() = images.size == 4 && !hasVideo
    val isSixImages: Boolean get() = images.size == 6 && !hasVideo
    val isNineOrLess: Boolean get() = images.size <= 9 && !hasVideo
}

/**
 * 评论数据模型
 */
@Parcelize
data class CircleComment(
    val id: String = "",
    val circleId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val content: String = "",
    val createTime: Long = 0L,
    val replyCount: Int = 0,
    val parentId: String? = null,
) : Parcelable
