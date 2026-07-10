package com.example.fragment.project.data.bean.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class AccompanySkillInfo(
    var accompanyReceiveSettingQueryRespList: List<GameInfo>?,
    val todayConsumeOrderCount: String
) {

    @Parcelize
    data class GameInfo(
        val id: String,
        val userId: String?,
        val categoryId: String?,
        val categoryName: String?,
        val categoryFileUrl: String?,
        var receiveStatus: String?,
        val maxStandard: String?,
    ) : Parcelable
}