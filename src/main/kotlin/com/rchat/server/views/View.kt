package com.rchat.server.views

class View {
    interface Message {}
    interface MessageWithId: Message {}
    interface UserWithId: Message {}
    interface AllWithId: MessageWithId, UserWithId {}
}