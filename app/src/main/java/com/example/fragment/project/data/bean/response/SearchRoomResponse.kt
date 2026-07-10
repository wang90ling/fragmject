package com.example.fragment.project.data.bean.response

data class SearchRoomResponse(
    val pageNo: Int,
    val pageSize: Int,
    val pages: Int,
    val records: List<SearchRoomRecord>?,
    val total: Int
)

data class SearchRoomRecord(
    val announcement: String,
    val followStatus: Int,
    val heatValue: String?,
    val heatValueStr: String?,
    val id: String,
    val mainLabel: String,
    val moduleId: String?,
    val moduleName: String?,
    val roomAvatar: String?,
    val roomName: String?,
    val roomNo: String,
    val roomStatus: Int,
    val roomType: Int?,
    val userAvatar: String?,
    val userName: String?,
    val welcomeMsg: String?,
    val channelId: String?,
    val kolId: String?,
)