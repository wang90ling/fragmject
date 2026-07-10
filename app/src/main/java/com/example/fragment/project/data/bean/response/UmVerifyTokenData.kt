package com.example.fragment.project.data.bean.response

data class UmVerifyTokenData(
    val carrierFailedResultData: String,
    val code: String,
    val msg: String,
    val requestCode: Int,
    val requestId: String,
    val token: String,
    val vendorName: String
)
