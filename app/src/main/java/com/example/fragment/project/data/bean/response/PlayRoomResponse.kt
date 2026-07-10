package com.example.fragment.project.data.bean.response

/**
 * 首页-娱乐-房间列表
 */

data class PlayRoomResponse(

    val pageNo: String? = null,
    val pageSize: String? = null,
    val total: String? = null,
    val pages: String? = null,
    val records: List<PlayRoomBean>?

){
    data class PlayRoomBean(

        val id: String? = null,
        val moduleId: String? = null,
        val heatValue: Long? = null,
        val followStatus: String? = null,
        val moduleName: String? = null,
        val mainLabel: String? = null,
        val micUsers: List<MicUsers?>? = null,
        val onlineCount: String? = null,
        val roomNo: String? = null,
        val roomName: String? = null,
        val roomAvatar: String? = null,
        val roomType: String? = null,
        val roomStatus: String? = null,
        val announcement: String? = null,
        val welcomeMsg: String? = null,
        val heatValueStr: String? = null,
        val userName: String? = null,
        val userAvatar: String? = null,
        val otherType: Int? = null,    //title: 扩展类型 1电台房 2游戏房
        val otherBusinessId: String? = null,
        val otherValue: String? = null,
        val otherAvatar: String? = null,
        val passwordSettings: Int? = null, // 1是锁，0是没锁
    ) {
        //是否空
        var isEmpty: Boolean = false
        //是否是Nj header
        var isNjHead: Boolean = false
        //顶部3个房间的header数据
        var headerRooms: List<PlayRoomBean>? = null
    }
}