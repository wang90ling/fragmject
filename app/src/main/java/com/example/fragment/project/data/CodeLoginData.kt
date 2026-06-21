package com.example.fragment.project.data

data class CodeLoginData(
    val baseAuthParams: String,
    val choiceFlag: Boolean,
    val firstLogin: Boolean,
    val loginFaceAuthFlag: Int,
    val token: String,
    val userId: String
)