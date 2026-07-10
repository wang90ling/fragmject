package com.example.fragment.project.data.bean.response

data class CategoryItem(
    val categoryName: String,
    val categoryType: String,  // "pc" or "mobile"
    val coverImageUrl: String?,
    val fileUrl: String,
    val id: String,
    val weighted: Int,
    var isSelected: Boolean = false,
    var sampleImageUrl: String? = null
)
