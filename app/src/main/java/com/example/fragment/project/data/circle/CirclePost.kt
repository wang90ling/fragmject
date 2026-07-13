package com.example.fragment.project.data.circle

import android.os.Parcelable
import android.text.Html
import android.util.Log
import com.example.fragment.project.R
import com.example.miaow.base.http.HttpResponse
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.math.abs

@Parcelize
data class CirclePostList(
    val data: CirclePostData? = null
) : HttpResponse(), Parcelable

@Parcelize
data class CirclePostData(
    val curPage: String = "",
    val datas: List<CirclePost>? = null,
    val offset: String = "",
    val over: Boolean = false,
    val pageCount: String = "0",
    val size: String = "",
    val total: String = ""
) : Parcelable

@Parcelize
data class CirclePost(
    val id: String = "0",
    val userId: String = "0",
    val userName: String = "",
    val userAvatar: String = "",
    val content: String = "",
    val mediaList: List<MediaItem>? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val shareCount: Int = 0,
    val createTime: String = "",
    val isLiked: Boolean = false,
    val topicId: String? = null,
    val topicName: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String? = null,
    val viewType: Int = 1
) : Parcelable {

    @IgnoredOnParcel
    private val avatarList: List<Int> = listOf(
        R.mipmap.avatar_1_raster,
        R.mipmap.avatar_2_raster,
        R.mipmap.avatar_3_raster,
        R.mipmap.avatar_4_raster,
        R.mipmap.avatar_5_raster,
        R.mipmap.avatar_6_raster,
    )

    @IgnoredOnParcel
    val avatarId by lazy {
        try {
            avatarList[abs(userId.toInt()) % 6]
        } catch (e: Exception) {
            Log.e("CirclePost", "compute avatarId failed: userId=$userId", e)
            R.mipmap.avatar_1_raster
        }
    }

    @IgnoredOnParcel
    val contentHtml by lazy {
        fromHtml(content)
    }

    val mediaUrls: List<MediaItem>
        get() = mediaList ?: emptyList()

    val imageCount: Int
        get() = mediaList?.count { it.type == MediaType.IMAGE } ?: 0

    val videoCount: Int
        get() = mediaList?.count { it.type == MediaType.VIDEO } ?: 0

    val hasVideo: Boolean
        get() = videoCount > 0

    val hasMedia: Boolean
        get() = !mediaList.isNullOrEmpty()

    private fun fromHtml(str: String): String {
        return try {
            Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY).toString()
        } catch (e: Exception) {
            str
        }
    }
}

@Parcelize
data class MediaItem(
    val url: String = "",
    val thumbnailUrl: String = "",
    val type: Int = MediaType.IMAGE,
    val width: Int = 0,
    val height: Int = 0,
    val duration: Long = 0,
    val size: Long = 0,
    val mimeType: String = "image/*"
) : Parcelable {

    val httpsUrl: String
        get() = url.replace("http://", "https://")

    val httpsThumbnailUrl: String
        get() = thumbnailUrl.replace("http://", "https://")

    val aspectRatio: Float
        get() = if (height > 0 && width > 0) width.toFloat() / height.toFloat() else 1f

    val isImage: Boolean
        get() = type == MediaType.IMAGE

    val isVideo: Boolean
        get() = type == MediaType.VIDEO

    val durationText: String
        get() {
            if (duration <= 0) return ""
            val seconds = duration / 1000
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return if (minutes > 0) {
                String.format("%d:%02d", minutes, remainingSeconds)
            } else {
                String.format("0:%02d", remainingSeconds)
            }
        }
}

object MediaType {
    const val IMAGE = 1
    const val VIDEO = 2
}

@Parcelize
data class CommentItem(
    val id: String = "0",
    val postId: String = "",
    val userId: String = "0",
    val userName: String = "",
    val userAvatar: String = "",
    val content: String = "",
    val createTime: String = "",
    val likeCount: Int = 0,
    val isLiked: Boolean = false,
    val replyCount: Int = 0,
    val replyList: List<CommentItem>? = null,
    val parentId: String? = null,
    val replyToUserId: String? = null,
    val replyToUserName: String? = null
) : Parcelable {

    @IgnoredOnParcel
    private val avatarList: List<Int> = listOf(
        R.mipmap.avatar_1_raster,
        R.mipmap.avatar_2_raster,
        R.mipmap.avatar_3_raster,
        R.mipmap.avatar_4_raster,
        R.mipmap.avatar_5_raster,
        R.mipmap.avatar_6_raster,
    )

    @IgnoredOnParcel
    val avatarId by lazy {
        try {
            avatarList[abs(userId.toInt()) % 6]
        } catch (e: Exception) {
            R.mipmap.avatar_1_raster
        }
    }
}

@Parcelize
data class TopicItem(
    val id: String = "0",
    val name: String = "",
    val description: String = "",
    val postCount: Int = 0,
    val followCount: Int = 0,
    val isFollowed: Boolean = false,
    val coverUrl: String = ""
) : Parcelable
