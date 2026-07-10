package com.example.fragment.project.data.bean.response

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class LabelListItem(
//    val fontColor: String?,
    val id: String,
    val labelName: String?,
    val labelType: String?,
    val labelTypeName: String?,
//    val weight: Int?
) : Parcelable {
    @IgnoredOnParcel
    @Transient
    var isSelected: Boolean = false
}
