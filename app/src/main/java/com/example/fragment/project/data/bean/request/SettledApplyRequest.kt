package com.example.fragment.project.data.bean.request

data class SettledApplyRequest(
    val categoryId: String,
    val picture: List<String>,
    val audio: String,
    val video: String,
    val contact: String?,
    val introduced: String?,
    val audioDuration: Int,
    val infoMap: Map<String, String>,
    var accompanyStandard: String,
    var id: String = "",
)