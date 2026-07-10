package com.example.fragment.project.data.bean.response

import kotlinx.parcelize.IgnoredOnParcel

data class ChannelUserItem(
    val id: String,
    val channelId: String? = null,
    val userId: String? = null,
    var role: String? = null,
    val channelOperateType: Int? = null,
    val channel: String? = null,
    val userInfo: UserInfo? = null,
) {
    @IgnoredOnParcel
    @Transient
    var type: Int? = null

    data class UserInfo(
        val userNo: String?,
        val sex: String?,
        val nickName: String?,
        val avatar: String?,
        val onlineFlag: Int?,
        val accompanyId: String?,
        val introduced: String?,
        val level: Int?,
        val accompanyLevel: String?,
        val onlineTime: String?
    )
}
