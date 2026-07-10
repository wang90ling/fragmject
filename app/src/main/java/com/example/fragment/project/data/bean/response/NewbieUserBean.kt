package com.example.fragment.project.data.bean.response

/**
 * 萌新用户
 *
 *{
 *   "request_id": "string",
 *   "code": 0,
 *   "message": "string",
 *   "data": [
 *     {
 *       "id": 0,
 *       "userNo": "string",
 *       "sex": "string",
 *       "nickName": "string",
 *       "avatar": "string",
 *       "birthday": "string",
 *       "rechargeFlag": 0,
 *       "remainChatNum": 0,
 *       "isChatUp": 0
 *     }
 *   ]
 * }
 */

data class NewbieUserBean(
    val id: String? = null,//用户ID
    val userNo: String? = null, //用户UID
    val sex: String? = null, //性别
    var nickName: String? = null, //昵称
    val avatar: String? = null, //头像
    val birthday: String? = null, //生日
    val rechargeFlag: Int? = null, //充值标记（1:是，0:否）
    val remainChatNum: Int? = null, //剩余聊天次数
    val isChatUp: Int? = null, //是否已撩（1:是，0:否）
)
