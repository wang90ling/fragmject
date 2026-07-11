package com.example.fragment.project.data.bean

import com.example.miaow.base.http.HttpResponse

data class BaseResponse<T>(
    val request_id: String = "",
    val data: T? = null
) : HttpResponse() {
    fun isSuccess(): Boolean {
        return code == "200" || errorCode == "200"
    }
}