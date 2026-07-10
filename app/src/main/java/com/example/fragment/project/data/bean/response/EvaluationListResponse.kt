package com.example.fragment.project.data.bean.response

data class EvaluationListResponse(
    val pageNo: String,
    val pageSize: String,
    val total: String,
    val pages: String,
    val records: List<EvaluationItem>?
) {
    data class EvaluationItem(
        val id: String,
        val evaluateUserInfo: EvaluateUserInfo?,
        val score: Int?,
        val content: String?,
        val createTime: String?,
        val anonymousFlag: Int?,
    ) {
        data class EvaluateUserInfo(
            val id: String,
            val avatar: String?,
            val nickName: String?,
//          val userPropListDto: UserPropListDto?
        )
    }
}
