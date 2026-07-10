package com.example.fragment.project.data.bean

data class SystemMessageBean(
    val id: String? = null,
    val senderId: String? = null,
    val receiverId: String? = null,
    val stationMsgTypeCode: String? = null,
    val stationMsgTitle: String? = null,
    val stationMsgContent: String? = null,
    val isRead: Boolean? = null,
    val status: Int? = null,
    val isDelete: Boolean? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
    val redirectType: String? = null,
    val redirectLink: String? = null
)
