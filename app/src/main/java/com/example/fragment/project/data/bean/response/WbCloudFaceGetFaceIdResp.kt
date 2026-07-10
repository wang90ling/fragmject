package com.example.fragment.project.data.bean.response

/**
 * {
 *     "code": "0",//0：成功 非0：失败
 *     "msg": "请求成功",
 *     "bizSeqNo": "业务流水号",//请求业务流水号
 *     "result": {
 *         "bizSeqNo": "业务流水号",
 *         "transactionTime": "20201019110305",
 *         "orderNo": "合作方订单号",//订单编号
 *         "faceId": "175177e03bc53d57222418e18c731488",//此次刷脸用户标识，调 SDK 时传入,faceId 有效期为5分钟，每次进行人脸核身都需要重新获取。
 *         "success": false //success：false 无意义，合作伙伴可忽略，无需处理。
 *     },
 *     "transactionTime": "20201019110305"
 * }
 */
data class WbCloudFaceGetFaceIdResp(
    val code: String,
    val msg: String,
    val bizSeqNo: String,
    val result: Result,
    val transactionTime: String
) {
    data class Result(
        val bizSeqNo: String,
        val transactionTime: String,
        val orderNo: String,
        val faceId: String,
        val success: Boolean
    )
}