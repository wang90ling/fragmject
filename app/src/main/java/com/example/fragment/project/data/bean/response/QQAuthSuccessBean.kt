package com.example.fragment.project.data.bean.response

data class QQAuthSuccessBean(
    val ret: Int,
    val openid: String,
    val access_token: String,
    val pay_token: String,
    val expires_in: Long,
    val pf: String,
    val pfkey: String,
    val msg: String,
    val login_cost: Int,
    val query_authority_cost: Int,
    val authority_cost: Int,
    val expires_time: Long
)
