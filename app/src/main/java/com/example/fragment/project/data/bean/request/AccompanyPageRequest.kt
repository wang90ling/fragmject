package com.example.fragment.project.data.bean.request

data class AccompanyPageRequest(
    val pageNo: Int,
    val pageSize: Int,
    val categoryId: String,
    val sex: String?,
    val query: Map<String, Any>?
)
