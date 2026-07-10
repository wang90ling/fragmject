package com.example.fragment.project.data.bean.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CategoryFilterItem(
    val attributeType: String,
    val coverShowFlag: Int,
    var dict: List<DictItem>,
    val filterFlag: Int,
    val hiddenFlag: Int,
    val inputType: String,
    val required: Int,
    val showName: String,
    val weighted: Int
) : Parcelable

@Parcelize
data class DictItem(
    val code: String, val value: String, val weighted: Int, var selected: Boolean = false
) : Parcelable
