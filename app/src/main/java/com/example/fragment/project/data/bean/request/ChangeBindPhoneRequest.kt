package com.example.fragment.project.data.bean.request

data class ChangeBindPhoneRequest(val oldTelephone: String, val telephone: String,val oldPhoneCountryCode:String,var phoneCountryCode:String, val rid: String)
