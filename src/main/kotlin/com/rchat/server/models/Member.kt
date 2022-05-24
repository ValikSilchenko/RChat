package com.rchat.server.models

import javax.persistence.*

@Entity
@Table(name = "members")
open class Member {
    @EmbeddedId
    open var id: MemberId? = null

    @MapsId("channelId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    open var channel: Channel? = null

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    open var user: Users? = null

    constructor() {}

    constructor(id: MemberId, channel: Channel, user: Users) {
        this.id = id
        this.channel = channel
        this.user = user
    }
}