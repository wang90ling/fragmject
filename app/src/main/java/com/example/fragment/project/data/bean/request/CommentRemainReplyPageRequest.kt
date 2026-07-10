package com.example.fragment.project.data.bean.request

data class CommentRemainReplyPageRequest(
    val pageNo: Int,
    val pageSize: Int,
    val postId: String,
    val parentCommentId: String,
    val maxTime: String
)