package com.example.fragment.project.data.bean.response

import kotlinx.parcelize.IgnoredOnParcel
import pw.z.baselibrary.bean.GuardianWornMedalDto
import pw.z.baselibrary.bean.RoomLevelConfigDto
import pw.z.baselibrary.bean.UserLevelBean
import pw.z.baselibrary.bean.UserPropDetailDto

data class UserDetailResponse(
    val accompanyLevel: String? = null,
    val authFlag: Int,
    var avatar: String?,
    var birthday: String?,
    val childMode: Int,
    val childPassword: String?,
    var constellation: String?,
    var coverImage: String?,
    val currentTime: String,
    var hometown: String?,
    var interestLabel: List<InterestLabel?>?,
    var introduced: String?,
    val level: Int,
    val name: String?,
    var nickName: String,
    var sex: String?,
    val telephone: String,
    val userId: String,
    val userNo: String,
    val luckyNo: String? = "",      //  靓号
    val userPropListDto: List<UserPropListDto>?,
    val userPropDetailDto: UserPropDetailDto?,
    val roomLevelConfigDto: RoomLevelConfigDto?,
    val mainLabel: String?,
    val mainLabelId: String?,
    val moduleAvatar: String?,
    val userType: String?,
    val roomMedal: String?,
    val avatarExpireTime: String? = null, //特权到期时间

    //  新增贵族等级
    val nobleLevelDto: UserLevelBean? = null,
    val guardianMedal: GuardianWornMedalDto? = null,
    val lastLoginFlag: Boolean,
    val personality: String? = null,
    //（证件提交标记 1：是，0：否）
    val certFlag: Int?,
    val charmIcon: String?, //魅力等级
) {
    //是否是自己主动修改的信息
    @IgnoredOnParcel
    @Transient
    var modifyByYourself = false

    fun isShowGuardian(): Boolean = mainLabelId == "4"

    //  用户是否有靓号有优先显示靓号没有显示正常号
    val userNum: String get() = luckyNo?.takeIf { it.isNotEmpty() } ?: userNo
    //  是否是靓号判断
    val isLuckyNo: Boolean get() = !luckyNo.isNullOrEmpty()

}

data class InterestLabel(
    val labelId: String,
    val labelName: String?
)

data class UserPropListDto(
    val customFlag: Int,
    val customInfo: String,
    val dynamicEffect: String?,
    val propFormat: String?,
    val propId: String,
    val propName: String,
    val propType: Int,
    val useFlag: Int,
    val userId: String
)



