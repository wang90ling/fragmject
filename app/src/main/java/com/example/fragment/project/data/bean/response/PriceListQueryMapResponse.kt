package com.example.fragment.project.data.bean.response

import com.google.gson.annotations.SerializedName

data class PriceListQueryMapResponse(
    @SerializedName("fun") val funLevel: List<AccompanyStandardItem>?,
    @SerializedName("god") val godLevel: List<AccompanyStandardItem>?,
    @SerializedName("technology") val technologyLevel: List<AccompanyStandardItem>?
)

data class AccompanyStandardItem(
    @SerializedName("accompanyStandard") val accompanyStandard: String,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("maxPrice") val maxPrice: String,
    @SerializedName("minPrice") val minPrice: String,
    @SerializedName("price") val price: String,
    @SerializedName("priceUnit") val priceUnit: String? = null,
    @SerializedName("receiveStatus") val receiveStatus: Int,
    @SerializedName("showName") val showName: String? = null,
    @SerializedName("skuId") val skuId: String,
    @SerializedName("skuName") val skuName: String?
){
    fun friendlyName(): String {
        return showName ?: skuName.orEmpty()
    }
}
