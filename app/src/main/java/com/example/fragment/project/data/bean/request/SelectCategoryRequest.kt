package com.example.fragment.project.data.bean.request

/**
 * 设置用户常玩游戏
 * {
 *   "categoryIds": [
 *     0
 *   ]
 * }
 *
 */
data class SelectCategoryRequest(

    val categoryIds: List<String>

)
