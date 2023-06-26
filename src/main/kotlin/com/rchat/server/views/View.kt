package com.rchat.server.views

class View {
    interface Message {}
    interface MessageWithId: Message {}
    interface Avatar {}
    interface UserWithId: Message, Avatar {}
    interface AllWithId: MessageWithId, UserWithId {}
}