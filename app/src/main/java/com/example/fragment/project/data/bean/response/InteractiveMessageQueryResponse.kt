package com.example.fragment.project.data.bean.response


/**
 * 站内信分页查询接口数据封装
 *
 * {
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
 *         "postId": 0,
 *         "mediaType": 0,
 *         "fileId": 0,
 *         "fileUrl": "string",
 *         "msgFileId": 0,
 *         "msgFileUrl": "string",
 *         "fromAccount": 0,
 *         "toAccount": 0,
 *         "commentId": 0,
 *         "parentCommentId": 0,
 *         "msgContent": "string",
 *         "createTime": "2025-12-23T10:27:30.994Z",
 *         "previewContent": "string",
 *         "msgType": "string",
 *         "fromPostAuthor": 0,
 *         "fromUserInfo": {
 *           "id": 0,
 *           "userNo": "string",
 *           "sex": "string",
 *           "nickName": "string",
 *           "avatar": "string",
 *           "onlineFlag": 0,
 *           "accompanyId": 0,
 *           "introduced": "string",
 *           "level": 0,
 *           "accompanyLevel": "string",
 *           "onlineTime": "2025-12-23T10:27:30.995Z"
 *         }
 *       }
 *     ]
 *   }
 * }
 *
 */

data class InteractiveMessageQueryResponse(
    val pageNo: Int? = null,
    val pageSize: String? = null,
    val total: String? = null,
    val pages: String? = null,
    val records: List<InteractiveMessageBean>?

)

