package com.example.miaow.base.http

import com.example.miaow.base.utils.GSonUtils
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import okhttp3.ResponseBody
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Retrofit/OkHttp 响应 → DTO 的转换器。
 *
 * 额外处理了两个兼容场景：
 * 1. 不同后端使用不同的错误码/错误信息字段名 —— wanandroid.com 返回 `errorCode` / `errorMsg`，
 *    而 apitest.dianta.pw 返回 `code` / `message`。这里在反序列化后把字段值双向回填，
 *    上层只需判断 `errorCode` 或 `code` 任意一个即可。
 * 2. 部分后端在错误响应中把 `data` 字段填成了字符串（例如 `data: "JSON parse error..."`），
 *    但 DTO 声明 `data: User?`。这里在反序列化前先把字符串型的 `data` 剥离，
 *    避免 `Expected BEGIN_OBJECT but was STRING` 崩溃，同时把文本并入 `message`。
 */
class GSonConverter : CoroutineHttp.Converter {

    companion object {
        fun create(): GSonConverter {
            return GSonConverter()
        }
    }

    private val gson = GSonUtils.gson

    override fun <T> converter(responseBody: ResponseBody, type: Class<T>): T {
        val raw = responseBody.charStream().readText()
        return fromJson(raw, type)
    }

    override fun <T> converter(responseBody: ResponseBody, type: Type): T {
        val raw = responseBody.charStream().readText()
        return fromJson(raw, type)
    }

    override fun <T> fromJson(json: String, classOfT: Class<T>): T {
        return fromJson(json, classOfT as Type)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> fromJson(json: String, typeOfT: Type): T {
        // 判断原始类型是否为 HttpResponse 子类，决定是否走预处理逻辑。
        val rawClass = when (typeOfT) {
            is Class<*> -> typeOfT
            is ParameterizedType -> typeOfT.rawType as? Class<*>
            else -> null
        }
        if (rawClass == null || !HttpResponse::class.java.isAssignableFrom(rawClass)) {
            return gson.fromJson(json, typeOfT)
        }

        // 预处理：统一字段名 + 剥离字符串型 data，避免子类 data:T? 解析崩溃。
        val sanitized = try {
            val root = JsonParser.parseString(json)
            if (root is JsonObject) {
                normalizeFields(root)
                stripStringData(root)
                root.toString()
            } else {
                json
            }
        } catch (_: Exception) {
            json
        }
        return gson.fromJson(sanitized, typeOfT)
    }

    /**
     * 把 `code` / `message` 与 `errorCode` / `errorMsg` 做双向同步，确保上层按任意一套字段
     * 判断都能拿到一致的结果。
     */
    private fun normalizeFields(root: JsonObject) {
        val code = root.get("code")
        val errorCode = root.get("errorCode")
        val message = root.get("message")
        val errorMsg = root.get("errorMsg")

        // code / errorCode：如果其中只有一个存在，就把值同步到另一个字段。
        when {
            code != null && !code.isJsonNull && (errorCode == null || errorCode.isJsonNull) -> {
                val str = primitiveToString(code) ?: return
                root.addProperty("errorCode", str)
            }
            errorCode != null && !errorCode.isJsonNull && (code == null || code.isJsonNull) -> {
                val str = primitiveToString(errorCode) ?: return
                root.addProperty("code", str)
            }
        }

        // message / errorMsg：同上
        when {
            message != null && !message.isJsonNull && (errorMsg == null || errorMsg.isJsonNull) -> {
                val str = primitiveToString(message) ?: return
                root.addProperty("errorMsg", str)
            }
            errorMsg != null && !errorMsg.isJsonNull && (message == null || message.isJsonNull) -> {
                val str = primitiveToString(errorMsg) ?: return
                root.addProperty("message", str)
            }
        }
    }

    /**
     * 如果 `data` 是一个字符串（例如服务端把错误信息塞进 data），
     * 把这段文本合并到 message，同时把 data 移除 —— 这样子类声明 `data: User?`
     * 不会再被字符串反序列化卡住。
     */
    private fun stripStringData(root: JsonObject) {
        val data = root.get("data") ?: return
        if (data is JsonPrimitive && data.isString) {
            val dataText = data.asString
            if (dataText.isNotBlank()) {
                val msg = primitiveToString(root.get("message"))
                    ?: primitiveToString(root.get("errorMsg"))
                    ?: ""
                if (msg.isBlank()) {
                    root.addProperty("message", dataText)
                    root.addProperty("errorMsg", dataText)
                }
            }
            root.remove("data")
        }
    }

    private fun primitiveToString(element: JsonElement?): String? {
        if (element == null || element.isJsonNull) return null
        if (element !is JsonPrimitive) return element.toString()
        return when {
            element.isNumber -> element.asNumber.toString()
            element.isBoolean -> element.asBoolean.toString()
            else -> element.asString
        }
    }
}