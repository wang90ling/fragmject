package com.example.fragment.project.data.bean.request

/**
 * 站内信分页查询: stationMessage/getPage
 *
 * {
 *   "pageNo": 0,
 *   "pageSize": 0,
 *   "receiverId": 0,
 *   "startId": 0,
 *   "stationMsgTypeCodes": [
 *     "string"
 *   ]
 * }
 *
 */


data class SystemMessageQueryRequest(

    val pageNo: Int,
    val pageSize: Int,
    val receiverId: String,
    //和服务端确认，下面参数不需要传
//    val startId: String,
//    val stationMsgTypeCodes: ArrayList<String>,

)