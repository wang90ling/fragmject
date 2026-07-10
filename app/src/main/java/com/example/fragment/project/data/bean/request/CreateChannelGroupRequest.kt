package com.example.fragment.project.data.bean.request

data class CreateChannelGroupRequest(
    val channelId: String,
    val groupName: String,
    val sort: Int = 0
)
