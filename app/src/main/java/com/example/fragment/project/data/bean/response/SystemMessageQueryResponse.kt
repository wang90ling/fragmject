package com.example.fragment.project.data.bean.response

import com.example.fragment.project.data.bean.SystemMessageBean


/**
 * 站内信分页查询接口数据封装
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
 *         "senderId": 0,
 *         "receiverId": 0,
 *         "stationMsgTypeCode": "string",
 *         "stationMsgTitle": "string",
 *         "stationMsgContent": "string",
 *         "isRead": true,
 *         "status": 0,
 *         "isDelete": true,
 *         "createTime": "2025-12-02T05:56:11.497Z",
 *         "updateTime": "2025-12-02T05:56:11.497Z",
 *         "redirectType": "string",
 *         "redirectLink": "string"
 *       }
 *     ]
 *   }
 * }
 *
 */

data class SystemMessageQueryResponse(
    val pageNo: Int? = null,
    val pageSize: String? = null,
    val total: String? = null,
    val pages: String? = null,
    val records: List<SystemMessageBean>?

)

