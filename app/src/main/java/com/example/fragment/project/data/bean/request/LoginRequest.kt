package com.example.fragment.project.data.bean.request

data class LoginRequest(
    val telephone: String,
    val phoneCountryCode: String,
    val password: String? = "",
    val code: String? = "",
)