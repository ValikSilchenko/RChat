package com.example.rchat.dataclasses

/* Класс-БД для хранения информации о сообщениях
*/
data class MessageItemDataClass(
    var incomingLogin: String,
    var incomingMessage: String,
    var incomingTime: String,
    var outgoingLogin: String,
    var outgoingMessage: String,
    var outgoingTime: String,
    var msgId: Int
)