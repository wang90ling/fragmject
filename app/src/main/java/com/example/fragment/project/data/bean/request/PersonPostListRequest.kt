package com.example.fragment.project.data.bean.request

data class PersonPostListRequest(
    val pageNo: Int,
    val pageSize: Int,
    val viewUserId: String,
)