package com.example.fragment.project.data.bean.response


/**
 * 圈子互动消息
 */
data class InteractiveMessageBean(

    val id: String? = null,
    val postId: String? = null,
    val mediaType: String? = null,
    val fileId: String? = null,
    val fileUrl: String? = null,
    val msgFileId: String? = null,
    val msgFileUrl: String? = null,
    val fromAccount: String? = null,
    val toAccount: String? = null,
    val msgContent: String? = null,
    val previewContent: String? = null,
    val msgType: String? = null,
    val createTime: Long? = null,
    val commentId: String? = null,
    val parentCommentId: String? = null,
    val fromPostAuthor: String? = null,
    val fromUserInfo: MsgUserInfo? = null,
    val toUserInfo: MsgUserInfo? = null,
    val receiveAccount: String? = null,

){
    data class MsgUserInfo(
        val id: String,
        val userNo: String?,
        val sex: String?,
        val nickName: String?,
        val avatar: String?,
        val onlineFlag: String?,
        val accompanyId: String?,
        val introduced: String?,
        val level: String?,
        val accompanyLevel: String?,
        val onlineTime: String?,

    )
}