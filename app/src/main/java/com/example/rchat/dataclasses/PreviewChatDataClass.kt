package com.example.rchat.dataclasses

/* Класс-БД для хранения информации о чатах (для списка чатов)
*/
data class PreviewChatDataClass(
    var login: String,
    var time: String,
    var message: String,
    var infoTxt: String,
    var unreadMsgCount: Int,
    var chatId: Int
)
