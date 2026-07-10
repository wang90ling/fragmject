package com.example.fragment.project.data.bean.response

data class AccompanyPriceItem(
    val minPrice: String,
    val maxPrice: String,
    var price: String?,
    val priceUnit: String?,
    val skuId: String,
    val skuName: String?,
    val showName: String?,
    val icon: String?
) {
    fun friendlyName(): String {
        return showName ?: skuName.orEmpty()
    }
}