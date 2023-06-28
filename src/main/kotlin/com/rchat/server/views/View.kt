package com.rchat.server.views

class View {
    interface Message
    interface MessageWithId: Message
    interface Avatar
    interface UserWithId: Message
    interface UserWithAvatar: UserWithId, Avatar
    interface AllWithId: MessageWithId, UserWithId
}