package pw.z.imchat.bean.response

data class PostListResponse(
    val list: List<PostItem>?
) {
    data class ReplyInfo(
        val id: String? = null,
        val content: String? = null,
        val userName: String? = null,
        val userId: String? = null
    )

    data class UserInfo(
        val userId: String? = null,
        val nickName: String? = null,
        val avatar: String? = null
    )
}

data class PostItem(
    val id: String? = null
)
