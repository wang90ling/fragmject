package com.example.fragment.project.data.bean.request

data class CategoryReceiveStatusSettingRequest(
    val categoryId: String?, val receiveStatus: Int, val accompanyStandard: String? = null
)
