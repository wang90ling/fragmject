package com.example.fragment.project.data.bean.response

data class SearchResponse(
    val accompanyLevel: Int,
    val age: Int?,
    val avatar: String,
    val categoryId: Any?,
    val categoryList: List<Category>,
    val coverImage: Any?,
    val grade: Any?,
    val introduced: String?,
    val level: Int,
    val nickName: String?,
    val orderAmount: Int,
    val scoreAvg: Double,
    val sex: String?,
    val userId: String,
    val userNo: String,
    val userPropListDto: Any?,
    val luckyNo: String? = "",
) {
    //  用户是否有靓号有优先显示靓号没有显示正常号
    val userNum: String get() = luckyNo?.takeIf { it.isNotEmpty() } ?: userNo ?: ""
    //  是否是靓号判断
    val isLuckyNo: Boolean get() = !luckyNo.isNullOrEmpty()
}
