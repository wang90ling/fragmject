package com.example.fragment.project.data.bean.response

data class StreamerRecommendBean(
    val pageNo: Int,
    val pageSize: Int,
    val pages: Int,
    val records: List<StreamerRecommendRecord>?,
    val total: Int
)

data class StreamerRecommendRecord(
    val audio: String?,
    val audioDuration: String? = null,
    val avatar: String?,
    val identityTags: List<StreamerRecommendTags?>?,
    val nickName: String,
    val online: Boolean,
    val sex: String?,
    val specialMark: String?,
    val totalScore: String?,
    val userId: String,
    val userNo: String,
    var isAudioPlaying: Boolean = false
)

data class StreamerRecommendTags(
    val name: String?,
    val image: String?
)