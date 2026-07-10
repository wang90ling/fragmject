package com.example.fragment.project.data.bean.request

data class EvaluationListRequest(
    val pageNo: Int,
    val pageSize: Int,
    val accompanyUserId: String,
    val startTime: String? = null,
    val endTime: String? = null,
)