package com.example.fragment.project.data.bean.response


/**
 * 站内信未读接口数据封装
 *
 * {
 *   "request_id": "string",
 *   "code": 0,
 *   "message": "string",
 *   "data": {
 *     "totalCount": 0,
 *     "stationMsgTitle": "string",
 *     "stationMsgContent": "string",
 *     "createTime": "2025-12-02T06:10:16.783Z"
 *   }
 * }
 *
 */

data class SystemMessageUnreadResponse(
    val totalCount: Int,
    val stationMsgTitle: String,
    val stationMsgContent: String,
    val createTime: String
)
