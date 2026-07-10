package com.example.fragment.project.data.bean.request

/**
 * 站内信未读: stationMessage/unread/count
 *
 * {
 *   "userId": 0,
 * }
 *
 */

data class SystemMessageUnreadRequest(
    val userId: Int,
)