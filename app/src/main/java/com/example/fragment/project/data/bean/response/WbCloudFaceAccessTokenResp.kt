package com.example.fragment.project.data.bean.response

/**
 *
 * {
 *   "code":"0",//0：成功 非0：失败
 *   "msg":"请求成功",//请求结果描述
 *   "transactionTime":"20151022043831",//调用接口的时间
 *   "access_token":"accessToken_string",//access_token 的值
 *   "expire_time":"20151022043831",// access_token 失效的绝对时间，由于各服务器时间差异，不能以此作为有效期的判定依据，只作为展示使用。
 *   "expire_in":7200// access_token 的最大生存时间，单位：秒，合作伙伴在判定有效期时以此为准。
 * }
 */
data class WbCloudFaceAccessTokenResp(
    val code: String,
    val msg: String,
    val transactionTime: String,
    val access_token: String,
    val expire_time: String,
    val expire_in: Int
)