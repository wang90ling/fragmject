package com.example.fragment.project.data.bean

import com.example.miaow.base.http.HttpResponse

data class BaseResponse<T>(
    val request_id: String = "",
    override var code: String = "",
    override var message: String? = null,
    val data: T? = null
) : HttpResponse(errorCode = "", errorMsg = "") {
    fun isSuccess(): Boolean {
        return code == "200"
    }
}