package com.example.fragment.project.data.bean.request

data class OlBindPhoneRequest(
    val unionId: String,
    val openId: String,
    val telephone: String,
    var phoneCountryCode:String,
    val code: String,
    val rid: String
)
