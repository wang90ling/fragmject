package com.example.fragment.project.data.bean.response

data class AccompanyResponse(
    val imageAuditStatus: Int?,//图片审核状态 0:审核中 1:审核通过 2:审核不通过
    val auditImage: String?//只有审核中时auditImage才有值
)