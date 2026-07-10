package com.example.fragment.project.data.bean.response

import kotlinx.parcelize.IgnoredOnParcel
import pw.z.imchat.bean.response.PostListResponse.ReplyInfo
import pw.z.imchat.bean.response.PostListResponse.UserInfo

data class CommentPageResponse(
    val pageNo: String,
    val pageSize: String,
    val total: String,
    val pages: String,
    val records: List<Comment>?
) {

    data class Comment(
        val id: String,
        val postId: String,
        val userId: String,
        val imageId: String,
        val image: String,
        val content: String,
        val replyCommentId: String,
        val replyUserId: String,
        val parentCommentId: String?,
        var likesCount: Long,
        var commentCount: Long,
        val createTime: String,
        var likedFlag: String?,
        val userInfo: UserInfo,
        val replyInfo: ReplyInfo?,
        var subList: List<Comment>?
    ) {
        @IgnoredOnParcel
        @Transient
        var payloadsType: String = ""

        fun isLiked(): Boolean {
            return likedFlag == "1"
        }

        fun setLiked(liked: Boolean) {
            this.likedFlag = if (liked) "1" else "0"
        }
    }
}