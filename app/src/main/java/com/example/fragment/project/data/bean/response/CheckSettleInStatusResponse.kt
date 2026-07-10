package com.example.fragment.project.data.bean.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckSettleInStatusResponse(
    val applyTime: String,
    val audio: String?,
    val auditStatus: Int,
    val categoryId: String,
    val categoryName: String?,
    val id: String,
    val infoMap: Map<String, String>,
    val payStatus: Int,
    val picture: List<String>?,
    val video: String?
) : Parcelable