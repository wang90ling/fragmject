package com.example.fragment.project.data.bean.response

/**
 *{
 *   "request_id": "string",
 *   "code": 0,
 *   "message": "string",
 *   "data": {
 *     "id": 0,
 *     "applyTime": "2026-03-05T07:57:24.429Z",
 *     "auditStatus": 0,
 *     "guildId": 0,
 *     "userId": 0
 *   }
 * }
 */

data class GuildApplyResponse(

    val id: String? = null,
    val applyTime: String? = null,
    val auditStatus: Int? = null,
    val guildId: String? = null,
    val userId: String? = null,

)

