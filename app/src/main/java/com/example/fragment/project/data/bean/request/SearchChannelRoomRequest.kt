package com.example.fragment.project.data.bean.request

data class SearchChannelRoomRequest(
    val pageNo: Int,
    val pageSize: Int,
    val fuzzymatch: String? = null
)
