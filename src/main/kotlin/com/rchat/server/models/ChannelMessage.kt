package com.rchat.server.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import com.rchat.server.views.View
import org.hibernate.annotations.Type
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "channel_messages")
open class ChannelMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.AllWithId::class)
    @Column(name = "msg_id", nullable = false)
    open var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties("hibernateLazyInitializer")
    @JoinColumn(name = "channel_id", nullable = false)
    open var channel: Channel? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties("hibernateLazyInitializer")
    @JsonView(View.Message::class)
    @JoinColumn(name = "sender_id", nullable = false)
    open var sender: Users? = null

    @JsonView(View.Message::class)
    @Column(name = "\"time\"", nullable = false)
    open var time: LocalTime? = null

    @JsonView(View.Message::class)
    @Column(name = "date", nullable = false)
    open var date: LocalDate? = null

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(View.Message::class)
    @Column(name = "message_text", nullable = false)
    open var messageText: String? = null

    constructor() {}

    constructor(id: Int, channel: Channel, sender: Users,
                time: LocalTime, date: LocalDate, messageText: String) {
        this.channel = channel
        this.sender = sender
        this.time = time
        this.date = date
        this.messageText = messageText
    }
}