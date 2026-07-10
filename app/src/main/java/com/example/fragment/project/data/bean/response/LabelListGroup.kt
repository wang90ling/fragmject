package com.example.fragment.project.data.bean.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LabelListGroup(
    val labelType: String?,
    val labelTypeName: String?,
    val items: List<LabelListItem>?,
) : Parcelable