package com.example.fragment.project.data.bean.response

data class VisitListResponse(
    val pageNo: String?,
    val pageSize: String?,
    val total: String?,
    val pages: String?,
    val records: List<Record>?
) {
    data class Record(
        val visitorId: String,
        val visitorUserNo: String?,
        val visitorNickname: String?,
        val visitorAvatar: String?,
        val visitorAge: String?,
        val visitorGender: String?,
        val visitTime: String?,
        val isRead: String?,
        var attentionStatus: String?,
        val onlineStatus: Int?,
        val lastOnlineTime: String?,
        val currentRoomId: String?
    ) {

        var isLastPageLast = false
    }
}