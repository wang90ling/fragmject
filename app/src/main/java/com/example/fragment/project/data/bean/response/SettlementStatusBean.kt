package com.example.fragment.project.data.bean.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettlementStatusBean(
    val accompanyApplyId: String,
    val accompanyType: String,
    val auditStatus: Int,
    val categoryFileUrl: String,
    val categoryId: String,
    val categoryName: String,
    val id: String,
    val reason: String?,
    val userId: String,
    var isConsumption: Boolean = false
) : Parcelable
