package com.example.fragment.project.data.bean.response

import pw.z.baselibrary.Constant
import pw.z.imchat.bean.DoRoomOrderItem

data class DistributePageResponse(
    val pageNo: String,
    val pageSize: String,
    val total: String,
    val pages: String,
    val records: List<DistributeOrder>?
) {
    data class DistributeOrder(
        val id: String,
        val categoryId: String? = null,
        val categoryName: String? = null,
        val priceUnit: String? = null,
        val tagsJsonArray: List<DoRoomOrderItem>? = null,
        val accompanyStandard: String? = null,
        val estimatedUnitPrice: String? = null,
        val peopleAmount: String? = null,
        val sex: String? = null,
        val remark: String? = null,
        val endTime: String? = null,
        val matchType: String? = null,
        val createTime: String? = null,
        val roomId: String? = null,
        var isGrab: Int? = null,
    ) {
        var itemViewType: Int? = null
        var payloadType: Int? = null
        var countdownTime: String = ""

        fun getEndTimeLong(): Long {
            return (endTime?.toLongOrNull() ?: 0)
        }
        fun isFromRoom(): Boolean {
            return Constant.MatchType.ROOM_MATCH == matchType
        }
    }
}
