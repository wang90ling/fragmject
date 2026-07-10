package com.example.fragment.project.data.bean.response

import com.google.gson.annotations.SerializedName

data class SettledApplyResponse(
    @SerializedName("applyTime")
    val applyTime: String,
    val audio: Any?,
    @SerializedName("auditStatus")
    val auditStatus: Any?,
    @SerializedName("categoryId")
    val categoryId: String,
    @SerializedName("categoryName")
    val categoryName: Any?,
    val id: String,
    @SerializedName("infoMap")
    val infoMap: Map<String, String>,
    val introduced: String,
    @SerializedName("payStatus")
    val payStatus: Any?,
    val picture: Any?,
    val telephone: Any?,
    val video: Any?
)
