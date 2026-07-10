package com.example.fragment.project.data.bean.response

/**
 * 	搭子权益信息响应参数{
 * userId	用户id[...]
 * levelName	搭子等级名称[...]
 * icon	搭子等级图标[...]
 * medal	勋章[...]
 * level	搭子等级[...]
 * levelScore	搭子成长值[...]
 * startPoints	开始积分[...]
 * endPoints	结束积分[...]
 * commissionRate	分成比例[...]
 * recommentLocation	推荐位置[...]
 * recommentLocationList	推荐位置集合[...]
 * replyUsable	评价自动回复权益标记(1:启用，0:禁用)[...]
 * recommendUsable	圈子曝光标记(1:启用，0:禁用)[...]
 * recommendRate	圈子曝光权益加成比例[...]
 * bubbleUsable	聊天气泡标记(1:启用，0:禁用)[...]
 * animationUsable	主页特效标记(1:启用，0:禁用)[...]
 * avatarUsable	头像框标记(1:启用，0:禁用)[...]
 * preferenceUsable	接单偏好权益标记(1:启用，0:禁用)[...]
 * exposeUsable	搭子列表曝光加成标记(1:启用，0:禁用)[...]
 * exposeRate	搭子列表曝光加成曝光加成比例[...]
 * costumeUsable	称号装扮标记(1:启用，0:禁用)[...]
 * mysteryUsable	神秘大礼包标记(1:启用，0:禁用)[...]
 * reachedUsable	首次到达等级奖励标记(1:启用，0:禁用)[...]
 * reachedPack	首次到达等级奖励礼包[...]
 * naturalUsable	自然月（1号）奖励标记(1:启用，0:禁用)[...]
 * naturalPack	自然月（1号）奖励礼包)[...]
 * }
 */
data class AccompanyUserRights(val replyUsable: Int)