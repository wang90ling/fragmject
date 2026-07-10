package com.example.fragment.project.data.bean.response

/**
 *  房间模块
 *
 * {
 *   "request_id": "string",
 *   "code": 0,
 *   "message": "string",
 *   "data": [
 *     {
 *       "id": 0,
 *       "moduleName": "string",
 *       "mainLabel": "string"
 *     }
 *   ]
 * }
 */

data class PlayRoomTabItem(
    val id: String,
    val moduleName: String,
    val mainLabel: String?,
)
