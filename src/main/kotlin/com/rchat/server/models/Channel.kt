package com.rchat.server.models

import javax.persistence.*

@Entity
@Table(name = "channels")
open class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    open var owner: Users? = null

    @Column(name = "channel_name", nullable = false, length = 20)
    open var channelName: String? = null

    constructor() {}

    constructor(id: Int, owner: Users, channelName: String) {
        this.owner = owner
        this.channelName = channelName
    }
}