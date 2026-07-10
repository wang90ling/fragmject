package com.example.fragment.project.data.bean.request


/**
 * 取消“派单抢单” /order/distribute/cancelSnatchingDispatchOrder
 *
 * {
 *   "distributeOrderId": 0
 * }
 *
 */

data class DispatchOrderCancelRequest(

    val distributeOrderId: String,

)