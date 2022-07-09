package com.example.rchat.dataclasses

data class PreviewChatDataClass(
    var login: String,
    var time: String,
    var message: String,
    var infoTxt: String,
    var unreadMsgCount: Int,
    var chatId: Int
)
