package com.example.fragment.project.data.bean.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubjectItem(
    val id: String, val name: String, var isSelected: Boolean = false
) : Parcelable
