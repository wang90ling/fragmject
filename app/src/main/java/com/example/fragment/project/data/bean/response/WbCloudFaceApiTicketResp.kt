package com.example.fragment.project.data.bean.response

/**
 * {
 *       "code": "0", //0：成功 非0：失败
 *       "msg": "请求成功",  //请求结果描述
 *       "transactionTime": "20151022044027", //调用接口的时间
 *       "tickets": [//ticket 返回数组
 *         {
 *              "value": "ticket_string",//ticket 的值
 *              "expire_time": "20151022044027"//ticket 失效的绝对时间
 *              "expire_in": "3600",//ticket 的最大生存时间
 *         }
 *     ]
 * }
 */
data class WbCloudFaceApiTicketResp(
    val code: String, val msg: String, val transactionTime: String, val tickets: List<Ticket>
) {
    data class Ticket(val expire_in: Int, val expire_time: String, val value: String)
}