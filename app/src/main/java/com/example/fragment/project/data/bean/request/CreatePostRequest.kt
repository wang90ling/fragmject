package com.example.fragment.project.data.bean.request

data class CreatePostRequest(
    val content: String,
    val mediaType: Int?,
//    val categoryId: String,
//    val goodsId: String,
    val files: List<String>?,
    val subjectIds: List<Long>?,
    val visibleScope: Int?
)
