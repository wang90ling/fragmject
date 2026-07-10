package com.example.fragment.project.data.bean.request

data class ConsumePendingRequest(
    val orderId: String,
    val pendingFlag: String,//	挂起标记（1:已挂起，0:未挂起）
    val reason: String,
    val files: List<File>?,
) {
    data class File(
        val fileUrl: String
    )
}
