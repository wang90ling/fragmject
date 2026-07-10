package com.example.fragment.project.data.bean.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChannelOwner(
    val id: String?,
    val channelId: String?,
    val userId: String?,
    val role: String?,
    val channelOperateType: String?,
    val channel: String?,
    val userInfo: UserInfo?,
) : Parcelable {
    
    @Parcelize
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
    ) : Parcelable
}
