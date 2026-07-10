package com.example.fragment.project.data.bean.request

data class ConfirmCompletionRequest(
    val id: String,
    val video: String? = null,
    val fileUrls: List<String>? = null
)
