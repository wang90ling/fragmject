package com.example.fragment.project.data.bean.response

import pw.z.baselibrary.bean.RoomLevelConfigDto
import pw.z.baselibrary.bean.UserPropDetailDto

data class HomeRecommend(
    val pageNo: String,
    val pageSize: String,
    val pages: String,
    val records: List<UserRecord>,
    val total: String
)

data class UserRecord(
    val accompanyLevel: Int,
    val avatar: String,
    val categoryId: String?,
    val categoryList: List<Category>?,
    val coverImage: String?,
    val grade: String?,
    val introduced: String?,
    val level: Int,
    val nickName: String,
    val orderAmount: Int,
    val scoreAvg: Double,
    val sex: String?,
    val userId: String,
    val userPropListDto: List<UserPropListDto>?, // 根据JSON数据，这个数组始终为空,
    val userPropDetailDto: UserPropDetailDto?,
    val onlineFlag: Int?,
    val roomMedal: String?,
    val roomLevelConfigDto: RoomLevelConfigDto?,
    var isAudioPlaying: Boolean = false
)

data class Category(
    val maxStandard: String? = null,
    val categoryCoverImageUrl: String? = null,
    val categoryFileUrl: String,
    val categoryId: String,
    val categoryName: String,
    val field1: String? = null,
    val field2: String? = null,
    val field3: String? = null,
    val field4: String? = null,
    val field5: String? = null,
    val field6: String? = null,
    val field7: String? = null,
    val field8: String? = null,
    val field9: String? = null,
    val field10: String? = null,
    val field11: String? = null,
    val field12: String? = null,
    val field13: String? = null,
    val field14: String? = null,
    val field15: String? = null,
    val fileList: List<FileItem>? = null,
    val id: String,
    val priceList: List<Price>? = null, // 根据JSON数据，这个字段有时为null
    val userId: String,
    val voiceIntroduced: String? = null
)

data class Price(
    val icon: String?,
    val maxPrice: Any?,
    val minPrice: Any?,
    val price: Double,
    val priceUnit: String?,
    val showName: String?,
    val skuId: String,
    val skuName: String
)

data class FileItem(
    val audioDuration: String? = null,
    val accompanyCategoryId: String,
    val fileId: String,
    val fileType: String, // "video", "picture" 或 "audio"
    val fileUrl: String,
    val sort: Int?
)
