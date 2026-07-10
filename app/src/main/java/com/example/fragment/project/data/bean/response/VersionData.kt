package com.example.fragment.project.data.bean.response

data class VersionData(
    val createTime: String,
    val downloadUrl: String,
    val effectTime: String,
    val forceUpdate: Int,
    val id: String,
    val packageName: String,
    val platform: String,
    val updateDescription: String,
    val versionCode: String,
    val versionIntro: String
)