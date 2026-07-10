package com.example.fragment.project.data.bean.request

import kotlinx.parcelize.IgnoredOnParcel

data class CommentAddRequest(
    val postId: String,
    val image: String?,
    val content: String,
    val replyCommentId: String?,
    val parentCommentId: String?,
) {
    @IgnoredOnParcel
    @Transient
    var level = 0
}
