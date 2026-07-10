package com.example.fragment.project.data.bean.response

import com.google.gson.annotations.SerializedName

// 首页公告
data class BulletinItem(
    val content: String,
    val id: String,
    @SerializedName("rollingShow")
    val isRollingShow: Int, // 1表示展示，0表示不展示
    @SerializedName("showTimeEnd")
    val showTimeEndMillis: String, // 时间戳字符串
    @SerializedName("showTimeStart")
    val showTimeStartMillis: String, // 时间戳字符串
    val subTitle: String,
    val title: String
)
