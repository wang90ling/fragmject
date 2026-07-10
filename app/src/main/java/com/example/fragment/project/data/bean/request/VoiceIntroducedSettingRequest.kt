package com.example.fragment.project.data.bean.request

data class VoiceIntroducedSettingRequest(
    val categoryId: String,
    val fileUrl: String? = null,
    val audioDuration: String? = null,
    val introduced: String? = null
)