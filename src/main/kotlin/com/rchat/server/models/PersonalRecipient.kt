package com.rchat.server.models

import javax.persistence.*

@Entity
@Table(name = "personal_recipient")
open class PersonalRecipient {
    @EmbeddedId
    open var id: PersonalRecipientId? = null

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: User? = null

    @MapsId("msgId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "msg_id", nullable = false)
    open var msg: PersonalMessage? = null

    constructor() {}

    constructor(id: PersonalRecipientId, user: User, msg: PersonalMessage) {
        this.id = id
        this.user = user
        this.msg = msg
    }
}