package com.example.fragment.project.data.bean.request

/**
 * 查询动态互动消息
 *
 *  {
 *      "pageNo": 0,
 *      "pageSize": 0,
 *      "maxTime": "2025-12-23T06:36:26.427Z"
 *  }
 *
 */


data class InteractiveMessageQueryRequest(

//    val pageNo: Int,
    val pageSize: Int,
    val maxTime: String,

)