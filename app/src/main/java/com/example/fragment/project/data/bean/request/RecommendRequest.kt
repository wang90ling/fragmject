package com.example.fragment.project.data.bean.request

data class RecommendRequest(
    val pageNo: Int,
    val pageSize: Int = 10,
    var categoryId: Long? = null,
    var sex: String? = null,
    var accompanyStandard: String? = null,
)
