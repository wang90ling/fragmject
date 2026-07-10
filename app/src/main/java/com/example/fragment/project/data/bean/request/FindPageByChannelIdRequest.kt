package com.example.fragment.project.data.bean.request

data class FindPageByChannelIdRequest(
//    val pageNo: Int,
    val pageSize: Int,
    val channelId: String,
    val endId: String?,
    val fuzzymatch: String?
)
