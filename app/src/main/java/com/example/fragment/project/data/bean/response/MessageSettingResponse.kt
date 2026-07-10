package com.example.fragment.project.data.bean.response

data class MessageSettingResponse(
    val id: String,
    var groupInteractionNotificationEnabled: Boolean,
    var orderNotificationEnabled: Boolean,
    var orderStatusChangeNotificationEnabled: Boolean,
    var privateChatNotificationEnabled: Boolean,
    var systemNotificationEnabled: Boolean,
    var dispatchSquareNotificationEnabled: Boolean,
)