package com.example.miaow.base.http

/**
 * 基础响应模型。
 *
 * 为了兼容多套后端接口的字段约定，
 * - `errorCode` / `errorMsg`：wanandroid.com 的返回字段；
 * - `code` / `message`：apitest.dianta.pw 等新服务的返回字段；
 * - `data`：业务载荷，在错误场景下有时被后端作为"错误描述串"返回，
 *   需在 [com.example.miaow.base.http.GSonConverter] 中做容错，
 *   避免 `data: T?` 反序列化时抛出 `Expected BEGIN_OBJECT but was STRING`。
 *
 * 各字段都保留为 `var`，便于 Converter 在反序列化失败时回填 fallback 值。
 */
open class HttpResponse @JvmOverloads constructor(
    var errorCode: String = "",
    var errorMsg: String = ""
) {
    /** 兼容 apitest.dianta.pw 返回的 `code` 字段。 */
    open var code: String = ""
    /** 兼容 apitest.dianta.pw 返回的 `message` 字段。 */
    open var message: String? = null

    var time = System.currentTimeMillis()

    fun setRequestTime(reqTime: Long) {
        time -= reqTime
    }
}