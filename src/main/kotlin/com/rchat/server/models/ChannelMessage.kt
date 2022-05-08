package com.rchat.server.models

import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "channel_messages")
open class ChannelMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msg_id", nullable = false)
    open var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    open var channel: Channel? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    open var sender: User? = null

    @Column(name = "\"time\"", nullable = false)
    open var time: LocalTime? = null

    @Column(name = "date", nullable = false)
    open var date: LocalDate? = null

    @Lob
    @Column(name = "message_text", nullable = false)
    open var messageText: String? = null

    constructor() {}

    constructor(id: Int, channel: Channel, sender: User,
                time: LocalTime, date: LocalDate, messageText: String) {
        this.id = id
        this.channel = channel
        this.sender = sender
        this.time = time
        this.date = date
        this.messageText = messageText
    }
}