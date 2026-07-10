package com.example.fragment.project.data.bean.request

/**
 * 公会关键词检索: /guild/keywordSearch
 *
 */

data class GuildSearchRequest(

    val pageNo: Int,
    val pageSize: Int,
    val keyword: String? = "",
    // 审核状态 0-审核中 1-审核通过 2-审核不通过
    val auditStatus: Int? = null,

)