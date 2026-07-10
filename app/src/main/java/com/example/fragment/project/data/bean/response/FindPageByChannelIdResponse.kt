package com.example.fragment.project.data.bean.response

data class FindPageByChannelIdResponse(
    val pageNo: String,
    val pageSize: String,
    val total: String,
    val pages: String,
    val records: List<ChannelUserItem>?
)