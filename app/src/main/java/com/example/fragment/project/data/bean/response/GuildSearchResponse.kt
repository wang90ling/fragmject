package com.example.fragment.project.data.bean.response

import com.example.fragment.project.data.bean.GuildBean


/**
 *
 *{
 *   "request_id": "string",
 *   "code": 0,
 *   "message": "string",
 *   "data": {
 *     "pageNo": 0,
 *     "pageSize": 0,
 *     "total": 0,
 *     "pages": 0,
 *     "records": [
 *       {
 *         "id": 0,
 *         "guildName": "string",
 *         "guildAvatar": "string",
 *         "guildOwner": 0,
 *         "guildOwnerName": "string",
 *         "guildOwnerAvatar": "string",
 *         "auditStatus": 0
 *       }
 *     ]
 *   }
 * }
 *
 */

data class GuildSearchResponse(
    val pageNo: Int? = null,
    val pageSize: String? = null,
    val total: String? = null,
    val pages: String? = null,
    val records: List<GuildBean>?

)

