package com.example.fragment.project.data.bean

data class BaseResponse<T>(
    val request_id: String,
    val code: Int = 0,
    val message: String? = null,
    val data: T? = null
) {
    fun isSuccess(): Boolean {
        return code == 200
    }
}