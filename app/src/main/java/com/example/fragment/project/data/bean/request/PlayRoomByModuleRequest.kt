package com.example.fragment.project.data.bean.request

/**
 * 查询模块房间列表
 */
data class PlayRoomByModuleRequest(

    val pageNo: Int,
    val pageSize: Int,
    var moduleId: String

)
