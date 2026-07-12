package com.example.miaow.base.http

import android.content.Context
import android.util.Log
import com.example.miaow.base.debug.DebugBridge
import com.example.miaow.base.utils.FileUtil
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import java.io.File
import java.lang.reflect.Type

private const val TAG = "CoroutineHttp"

/**
 * get请求
 * @param init  http请求体
 */
suspend inline fun <reified T : HttpResponse> CoroutineScope.get(
    noinline init: HttpRequest.() -> Unit
): T {
    return CoroutineHttp.getInstance().get(init, T::class.java)
}

suspend inline fun CoroutineScope.string(
    noinline init: HttpRequest.() -> Unit
): String {
    return CoroutineHttp.getInstance().string(init)
}

/**
 * post请求（form-urlencoded 表单 body，即 @FieldMap 形式）。
 *
 * 适用于传统表单接口，例如 wanandroid.com 的 user/login / user/register 等。
 *
 * @param init  http请求体
 */
suspend inline fun <reified T : HttpResponse> CoroutineScope.post(
    noinline init: HttpRequest.() -> Unit
): T {
    return CoroutineHttp.getInstance().post(init, T::class.java)
}

/**
 * post请求（JSON body，Content-Type: application/json）。
 *
 * 适用于要求 JSON 请求体的接口（例如 apitest.dianta.pw 的 login/code 等），
 * 不会再把参数拼成 `code=xxx&phoneCountryCode=xxx` 形式发送出去。
 *
 * @param init  http请求体
 */
suspend inline fun <reified T : HttpResponse> CoroutineScope.postJson(
    noinline init: HttpRequest.() -> Unit
): T {
    return CoroutineHttp.getInstance().postJson(init, T::class.java)
}

/**
 * form请求
 * @param init  http请求体
 */
suspend inline fun <reified T : HttpResponse> CoroutineScope.form(
    noinline init: HttpRequest.() -> Unit
): T {
    return CoroutineHttp.getInstance().form(init, T::class.java)
}

/**
 * download请求
 * @param savePath 保存路径
 * @param fileName 文件名称
 * @param init  http请求体
 */
suspend inline fun CoroutineScope.download(
    savePath: String,
    fileName: String,
    noinline init: HttpRequest.() -> Unit
): HttpResponse {
    return CoroutineHttp.getInstance().download(savePath, fileName, init)
}

fun Context.setBaseUrl(baseUrl: String) {
    CoroutineHttp.getInstance().setBaseUrl(baseUrl)
}

fun Context.setHttpClient(client: OkHttpClient) {
    CoroutineHttp.getInstance().setHttpClient(client)
}

/**
 * 懒加载版本的 setHttpClient：provider 仅在首次发起请求时被调用，
 * 避免在 Application.onCreate 阶段在主线程同步创建 OkHttpClient，减少冷启动耗时。
 */
fun Context.setHttpClientLazy(provider: () -> OkHttpClient) {
    CoroutineHttp.getInstance().setClientProvider(provider)
}

fun Context.updateDefaultHeaders(headers: Map<String, String>) {
    CoroutineHttp.getInstance().setDefaultHeaders(headers)
}

/**
 * retrofit + coroutines 封装的Http工具类
 */
class CoroutineHttp private constructor() {

    companion object {

        @Volatile
        private var INSTANCE: CoroutineHttp? = null

        fun getInstance() = INSTANCE ?: synchronized(CoroutineHttp::class.java) {
            INSTANCE ?: CoroutineHttp().also { INSTANCE = it }
        }

    }

    private lateinit var baseUrl: String
    private var client: OkHttpClient? = null
    private var clientProvider: (() -> OkHttpClient)? = null
    private var defaultHeaders: Map<String, String> = emptyMap()
    private var retrofit: Retrofit? = null
    private var service: ApiService? = null
    private var converter: Converter? = null

    fun setBaseUrl(baseUrl: String) {
        this.baseUrl = baseUrl
    }

    fun setHttpClient(client: OkHttpClient) {
        this.client = client
        // 显式设置后丢弃之前可能存在的 provider，避免两者同时生效造成配置不一致。
        this.clientProvider = null
        // baseUrl / client 变更后，原有 retrofit 已失效，重置以保证下次调用重建
        retrofit = null
        service = null
    }

    fun setDefaultHeaders(headers: Map<String, String>) {
        defaultHeaders = headers.toMap()
    }

    fun updateDefaultHeaders(headers: Map<String, String>) {
        defaultHeaders = headers.toMap()
    }

    /**
     * 与 [setHttpClient] 二选一。provider 仅在第一次需要 OkHttpClient 时调用，
     * 让冷启动阶段不再同步构造 OkHttpClient（OkHttp + Cache + 拦截器 几十毫秒级的耗时）。
     */
    fun setClientProvider(provider: () -> OkHttpClient) {
        this.clientProvider = provider
    }

    /**
     * 获取当前 OkHttpClient：优先读显式设置的实例，其次从 provider 需要时创建并缓存。
     */
    @Synchronized
    private fun obtainClient(): OkHttpClient {
        client?.let { return it }
        val provider = clientProvider
            ?: error("OkHttpClient not configured: call setHttpClient or setHttpClientLazy first")
        return provider().also { client = it }
    }

    private fun getRetrofit(): Retrofit {
        return retrofit ?: Retrofit.Builder().baseUrl(baseUrl).client(obtainClient()).build().also {
            retrofit = it
        }
    }

    private fun getService(): ApiService {
        return service ?: getRetrofit().create(ApiService::class.java).also { service = it }
    }

    private fun getConverter(): Converter {
        return converter ?: GSonConverter.create().also { converter = it }
    }

    suspend fun <T : HttpResponse> get(
        init: HttpRequest.() -> Unit,
        type: Class<T>,
    ): T = get(HttpRequest().apply(init), type)

    /**
     * 接收已构造好的 [HttpRequest] 的重载，便于上层（如 SWR 缓存算子）先从 request 派生
     * cacheKey、再用同一个 request 发起网络请求，避免对 `init: HttpRequest.() -> Unit`
     * 反复 apply 造成的重复构造与 [HttpRequest.time] 漂移。
     */
    suspend fun <T : HttpResponse> get(
        request: HttpRequest,
        type: Class<T>,
    ): T {
        val headers = mergeHeaders(request.getHeader())
        return try {
            getService().get(request.getUrl(baseUrl), headers).body()?.let { body ->
                getConverter().converter(body, type).apply { setRequestTime(request.time) }
            } ?: buildResponse("-1", "response body is null", type)
        } catch (e: Exception) {
            Log.e(TAG, "GET ${request.getUrl(baseUrl)} failed", e)
            fallbackResponse(request, type, e)
        }
    }

    suspend fun <T : HttpResponse> post(
        init: HttpRequest.() -> Unit,
        type: Class<T>,
    ): T = post(HttpRequest().apply(init), type)

    /** 与 [get] 同名重载语义一致：复用上层已构造的 request，避免双 apply。 */
    suspend fun <T : HttpResponse> post(
        request: HttpRequest,
        type: Class<T>,
    ): T {
        val headers = mergeHeaders(request.getHeader())
        return try {
            getService().post(
                request.getUrl(baseUrl),
                headers,
                request.getParam()
            ).body()?.let { body ->
                getConverter().converter(body, type).apply { setRequestTime(request.time) }
            } ?: buildResponse("-1", "response body is null", type)
        } catch (e: Exception) {
            Log.e(TAG, "POST ${request.getUrl(baseUrl)} failed", e)
            fallbackResponse(request, type, e)
        }
    }

    suspend fun <T : HttpResponse> postJson(
        init: HttpRequest.() -> Unit,
        type: Class<T>,
    ): T = postJson(HttpRequest().apply(init), type)

    /** 与 [post] 对应，但请求体为 JSON：适用于要求 Content-Type: application/json 的接口。 */
    suspend fun <T : HttpResponse> postJson(
        request: HttpRequest,
        type: Class<T>,
    ): T {
        val headers = mergeHeaders(request.getHeader())
        return try {
            getService().postJson(
                request.getUrl(baseUrl),
                headers,
                request.getJsonBody()
            ).body()?.let { body ->
                getConverter().converter(body, type).apply { setRequestTime(request.time) }
            } ?: buildResponse("-1", "response body is null", type)
        } catch (e: Exception) {
            Log.e(TAG, "POST-JSON ${request.getUrl(baseUrl)} failed", e)
            fallbackResponse(request, type, e)
        }
    }

    /**
     * [postJson] 的 Type 重载：支持泛型响应（如 `BaseResponse<HomeRecommend>`）。
     * `Class<T>` 版本在反序列化时会丢失泛型参数，此重载通过 Gson [Type] 保留完整类型信息。
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> postJson(
        init: HttpRequest.() -> Unit,
        type: Type,
    ): T = postJson(HttpRequest().apply(init), type)

    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> postJson(
        request: HttpRequest,
        type: Type,
    ): T {
        val headers = mergeHeaders(request.getHeader())
        return try {
            getService().postJson(
                request.getUrl(baseUrl),
                headers,
                request.getJsonBody()
            ).body()?.let { body ->
                getConverter().converter(body, type) as T
            } ?: throw IllegalStateException("response body is null")
        } catch (e: Exception) {
            Log.e(TAG, "POST-JSON ${request.getUrl(baseUrl)} failed", e)
            throw e
        }
    }

    /**
     * [get] 的 Type 重载：支持泛型响应（如 `BaseResponse<List<CategoryItem>>`）。
     * `Class<T>` 版本在反序列化时会丢失泛型参数，此重载通过 Gson [Type] 保留完整类型信息。
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> get(
        init: HttpRequest.() -> Unit,
        type: Type,
    ): T = get(HttpRequest().apply(init), type)

    @Suppress("UNCHECKED_CAST")
    suspend fun <T : Any> get(
        request: HttpRequest,
        type: Type,
    ): T {
        val headers = mergeHeaders(request.getHeader())
        return try {
            getService().get(request.getUrl(baseUrl), headers).body()?.let { body ->
                getConverter().converter(body, type) as T
            } ?: throw IllegalStateException("response body is null")
        } catch (e: Exception) {
            Log.e(TAG, "GET ${request.getUrl(baseUrl)} failed", e)
            throw e
        }
    }

    suspend fun <T : HttpResponse> form(
        init: HttpRequest.() -> Unit,
        type: Class<T>,
    ): T {
        val request = HttpRequest().apply(init)
        val headers = mergeHeaders(request.getHeader())
        return try {
            getService().form(
                request.getUrl(baseUrl),
                headers,
                request.getMultipartBody()
            ).body()?.let { body ->
                getConverter().converter(body, type).apply { setRequestTime(request.time) }
            } ?: buildResponse("-1", "response body is null", type)
        } catch (e: Exception) {
            Log.e(TAG, "FORM ${request.getUrl(baseUrl)} failed", e)
            buildResponse("-1", e.message ?: e.javaClass.simpleName, type)
        }
    }

    suspend fun download(
        savePath: String,
        fileName: String,
        init: HttpRequest.() -> Unit
    ): HttpResponse {
        val request = HttpRequest().apply(init)
        val headers = mergeHeaders(request.getHeader())
        return try {
            val response = getService().get(request.getUrl(), headers)
            if (response.isSuccessful) {
                val file = File(savePath, fileName)
                response.body()?.byteStream()?.use { inputStream ->
                    file.writeBytes(inputStream.readBytes())
                }
            }
            buildResponse("0", "success", HttpResponse::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "DOWNLOAD ${request.getUrl()} failed", e)
            buildResponse("-1", e.message ?: e.javaClass.simpleName, HttpResponse::class.java)
        }
    }

    suspend fun string(
        init: HttpRequest.() -> Unit,
    ): String {
        val request = HttpRequest().apply(init)
        val headers = mergeHeaders(request.getHeader())
        return try {
            getService().get(
                request.getUrl(baseUrl),
                headers
            ).body()?.string().orEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "STRING ${request.getUrl(baseUrl)} failed", e)
            ""
        }
    }

    private fun mergeHeaders(requestHeaders: Map<String, String>): Map<String, String> {
        return if (defaultHeaders.isEmpty()) {
            requestHeaders
        } else {
            LinkedHashMap<String, String>(defaultHeaders.size + requestHeaders.size).apply {
                putAll(defaultHeaders)
                putAll(requestHeaders)
            }
        }
    }

    /**
     * 网络异常时的兜底逻辑：
     * - Debug 包尝试读取 assets/json/ 下的预置数据，方便离线调试与示例运行；
     * - Release 包不再读取本地 assets，直接返回错误响应，避免线上隐式行为。
     */
    private fun <T : HttpResponse> fallbackResponse(
        request: HttpRequest,
        type: Class<T>,
        e: Exception
    ): T {
        if (DebugBridge.allowAssetsFallback) {
            val jsonName = request.getUrl(baseUrl).replace("/", "-").replace("?", "_")
            val json = FileUtil.readAssetString("json/${jsonName}.json")
            if (json.isNotBlank()) {
                return getConverter().fromJson(json, type)
            }
        }
        return buildResponse("-1", e.message ?: e.javaClass.simpleName, type)
    }

    /**
     * 通过 JsonObject 构建错误响应，避免使用字符串拼接造成的 JSON 注入 / 非法 JSON 风险
     * （之前的实现仅替换双引号，遗漏了反斜杠、换行等其他特殊字符）。
     */
    private fun <T : HttpResponse> buildResponse(code: String, msg: String, type: Class<T>): T {
        val obj = JsonObject().apply {
            addProperty("errorCode", code)
            addProperty("errorMsg", msg)
        }
        return getConverter().fromJson(obj.toString(), type)
    }

    interface Converter {
        fun <T> converter(responseBody: ResponseBody, type: Class<T>): T

        fun <T> converter(responseBody: ResponseBody, type: Type): T

        @Throws(Exception::class)
        fun <T> fromJson(json: String, classOfT: Class<T>): T

        @Throws(Exception::class)
        fun <T> fromJson(json: String, typeOfT: Type): T
    }

}

interface ApiService {

    @POST
    suspend fun form(
        @Url url: String = "",
        @HeaderMap header: Map<String, String>,
        @Body body: MultipartBody
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST
    suspend fun post(
        @Url url: String = "",
        @HeaderMap header: Map<String, String>,
        @FieldMap params: Map<String, String>
    ): Response<ResponseBody>

    @POST
    suspend fun postJson(
        @Url url: String = "",
        @HeaderMap header: Map<String, String>,
        @Body body: RequestBody
    ): Response<ResponseBody>

    @GET
    suspend fun get(
        @Url url: String = "",
        @HeaderMap header: Map<String, String>
    ): Response<ResponseBody>
}

