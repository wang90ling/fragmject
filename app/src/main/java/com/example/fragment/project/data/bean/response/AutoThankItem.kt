package com.example.fragment.project.data.bean.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AutoThankItem(
    val id: String,
    val userId: String?,
    val type: String?,//1:1-2星评价 2:3星评价 3:4-5星评价
    var content: String?,
) : Parcelable