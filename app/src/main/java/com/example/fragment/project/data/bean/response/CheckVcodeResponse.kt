package com.example.fragment.project.data.bean.response

data class CheckVcodeResponse(
    val valid: Boolean,
    val rid: String,
    var phone: String? = null,
    var phoneCountryCode: String? = null,
    var isConsumption: Boolean
)
