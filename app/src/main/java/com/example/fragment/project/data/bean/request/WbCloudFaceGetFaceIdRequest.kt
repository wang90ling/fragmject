package com.example.fragment.project.data.bean.request

data class WbCloudFaceGetFaceIdRequest(
    val appId: String,
    val orderNo: String,
    val name: String,
    val idNo: String,
    val userId: String,
    val version: String,
    val sign: String,
    val nonce: String
)