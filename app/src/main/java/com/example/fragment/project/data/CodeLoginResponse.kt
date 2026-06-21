package com.example.fragment.project.data

data class CodeLoginResponse(
    val code: Int,
    val data: CodeLoginData,
    val message: String,
    val request_id: String
)