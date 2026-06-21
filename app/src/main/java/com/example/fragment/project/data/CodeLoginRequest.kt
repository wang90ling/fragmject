package com.example.fragment.project.data

/**
 * 验证码登录请求体。
 *
 * 验证码登录时 `password` 可以不传；如果后端不需要该字段，默认直接省略。
 */
data class CodeLoginRequest constructor(
    val phoneCountryCode: String,
    val telephone: String,
    val password: String? = null,
    val code: String,
)