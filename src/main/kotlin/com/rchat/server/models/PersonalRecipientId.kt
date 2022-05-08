package com.rchat.server.models

import org.hibernate.Hibernate
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity

@Embeddable
open class PersonalRecipientId : Serializable {
    @Column(name = "user_id", nullable = false)
    open var userId: Int? = null

    @Column(name = "msg_id", nullable = false)
    open var msgId: Int? = null

    override fun hashCode(): Int = Objects.hash(userId, msgId)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as PersonalRecipientId

        return userId == other.userId &&
                msgId == other.msgId
    }

    constructor() {}

    constructor(userId: Int, msgId: Int) {
        this.userId = userId
        this.msgId = msgId
    }

//    companion object {
//        private const val serialVersionUID = -8027061457275808411L
//    }
}