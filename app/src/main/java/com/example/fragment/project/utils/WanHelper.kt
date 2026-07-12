package com.example.fragment.project.utils

import android.util.Log
import com.example.fragment.project.database.AppDatabase
import com.example.fragment.project.data.CodeLoginData
import com.example.fragment.project.data.History
import com.example.fragment.project.data.User
import com.example.miaow.base.database.KVDatabase
import com.example.miaow.base.http.CoroutineHttp
import com.example.miaow.base.utils.GSonUtils
import com.example.miaow.base.utils.logD
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

/**
 * 数据持久化辅助类
 */
object WanHelper {

    private const val TAG = "WanHelper"
    private const val BOOKMARK = "bookmark"
    private const val BROWSE_HISTORY = "browse_history"
    private const val SCHEDULE = "schedule"
    private const val SEARCH_HISTORY = "search_history"
    private const val TOKEN_KEY = "user_token"
    private const val USER_DATA_KEY = "user_data"

    // 后端接口统一的认证请求头字段名
    private const val TOKEN_HEADER_KEY = "token"

    // Gson 线程安全，复用全局单例避免重复创建。
    private val gson get() = GSonUtils.gson
    private val scheduleListType = object : TypeToken<List<String>>() {}.type

    /**
     * 同步当前 Token 到全局默认请求头。
     * 设置、清除 Token 时都必须调用，确保后续请求自动携带/移除认证信息。
     */
    private fun syncTokenToHeaders(token: String?) {
        val headers = LinkedHashMap<String, String>().apply {
            put("Content-Type", "application/json")
            put("Accept", "application/json")
            put("x-device", "APP")
            if (!token.isNullOrBlank()) {
                put(TOKEN_HEADER_KEY, token)
            }
        }
        CoroutineHttp.getInstance().updateDefaultHeaders(headers)
    }

    suspend fun setBookmark(value: String, url: String) {
        val historyDao = AppDatabase.getHistoryDao()
        val history = historyDao.getByUrl(key = BOOKMARK, url = url)
        if (history != null) {
            historyDao.delete(history)
        }
        historyDao.insertWithLimitCheck(History(id = 0, key = BOOKMARK, value = value, url = url))
    }

    fun getBookmark(): Flow<List<History>> {
        return AppDatabase.getHistoryDao().getByKey(BOOKMARK)
    }

    suspend fun setBrowseHistory(value: String, url: String) {
        val historyDao = AppDatabase.getHistoryDao()
        val history = historyDao.getByUrl(key = BROWSE_HISTORY, url = url)
        if (history != null) {
            historyDao.delete(history)
        }
        historyDao.insertWithLimitCheck(History(id = 0, key = BROWSE_HISTORY, value = value, url = url))
    }

    fun getBrowseHistory(): Flow<List<History>> {
        return AppDatabase.getHistoryDao().getByKey(BROWSE_HISTORY)
    }

    /**
     * 设置搜索历史
     */
    suspend fun setSearchHistory(value: String) {
        val historyDao = AppDatabase.getHistoryDao()
        val history = historyDao.getByValue(key = SEARCH_HISTORY, value = value)
        if (history != null) {
            historyDao.delete(history)
        }
        historyDao.insertWithLimitCheck(History(id = 0, key = SEARCH_HISTORY, value = value))
    }

    /**
     * 获取搜索历史
     */
    fun getSearchHistory(): Flow<List<History>> {
        return AppDatabase.getHistoryDao().getByKey(SEARCH_HISTORY)
    }

    suspend fun deleteHistory(history: History) {
        AppDatabase.getHistoryDao().delete(history)
    }

    /**
     * 设置用户信息
     */
    suspend fun setUser(user: User) {
        AppDatabase.getUserDao().insert(user)
    }

    /**
     * 删除用户信息
     */
    suspend fun deleteUser(user: User) {
        AppDatabase.getUserDao().delete(user)
    }

    /**
     * 获取用户信息
     */
    fun getUser(): Flow<User?> {
        return AppDatabase.getUserDao().get()
    }

    suspend fun setSchedule(year: Int, month: Int, day: Int, list: MutableList<String>) {
        KVDatabase.set("${SCHEDULE}_${year}_${month}_${day}", gson.toJson(list))
    }

    suspend fun getSchedule(year: Int, month: Int, day: Int): MutableList<String> {
        return try {
            val json = KVDatabase.get("${SCHEDULE}_${year}_${month}_${day}")
            val parsed: List<String>? = gson.fromJson(json, scheduleListType)
            parsed?.let { ArrayList(it) } ?: ArrayList()
        } catch (e: Exception) {
            Log.e(TAG, "getSchedule failed", e)
            ArrayList()
        }
    }

    suspend fun setToken(token: String) {
        KVDatabase.set(TOKEN_KEY, token)
        syncTokenToHeaders(token)
    }

    suspend fun getToken(): String? {
        return try {
            KVDatabase.get(TOKEN_KEY)
        } catch (e: Exception) {
            Log.e(TAG, "getToken failed", e)
            null
        }
    }

    suspend fun clearToken() {
        KVDatabase.set(TOKEN_KEY, "")
        syncTokenToHeaders(null)
    }

    suspend fun isLoggedIn(): Boolean {
        logD("wangling getToken:"+getToken());
        return getToken()?.isNotBlank() == true
    }

    suspend fun setLoginData(data: CodeLoginData) {
        KVDatabase.set(USER_DATA_KEY, gson.toJson(data))
    }

    suspend fun getLoginData(): CodeLoginData? {
        return try {
            val json = KVDatabase.get(USER_DATA_KEY)
            if (json.isNotBlank()) {
                gson.fromJson(json, CodeLoginData::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "getLoginData failed", e)
            null
        }
    }

    suspend fun clearLoginData() {
        KVDatabase.set(USER_DATA_KEY, "")
    }

    suspend fun logout() {
        clearToken()
        clearLoginData()
        AppDatabase.getUserDao().clear()
    }

}