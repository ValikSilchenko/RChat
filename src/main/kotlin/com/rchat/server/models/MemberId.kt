package com.rchat.server.models

import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
open class MemberId : Serializable {
    @Column(name = "channel_id", nullable = false)
    open var channelId: Int? = null

    @Column(name = "user_id", nullable = false)
    open var userId: Int? = null

    override fun hashCode(): Int = Objects.hash(channelId, userId)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as MemberId

        return channelId == other.channelId &&
                userId == other.userId
    }

    constructor() {}

    constructor(channelId: Int?, userId: Int?) {
        this.channelId = channelId
        this.userId = userId
    }
}