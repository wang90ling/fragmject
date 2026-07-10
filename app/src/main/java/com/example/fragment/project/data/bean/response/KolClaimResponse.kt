package com.example.fragment.project.data.bean.response

data class KolClaimResponse(
    val rewards: List<KolRewardItem>? = null
)

data class KolRewardItem(
    val name: String? = null,
    val value: String? = null,
    val expireDays: String? = null,
    val count: String? = null,
    val dynamicEffect: String? = null,
    val propFormat: String? = null,
    val type: Int? = null,
)
