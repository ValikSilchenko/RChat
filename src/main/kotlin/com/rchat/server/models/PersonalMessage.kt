package com.rchat.server.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import com.rchat.server.views.View
import org.hibernate.annotations.Type
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "personal_messages")
open class PersonalMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.MessageWithId::class)
    @Column(name = "msg_id", nullable = false)
    open var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties("hibernateLazyInitializer")
    @JsonView(View.Message::class)
    @JoinColumn(name = "sender_id", nullable = false)
    open var sender: Users? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties("hibernateLazyInitializer")
    @JsonView(View.Message::class)
    @JoinColumn(name = "recipient_id", nullable = false)
    open var recipient: Users? = null

    @JsonView(View.Message::class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm")
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

    constructor(sender: Users, recipient: Users, time: LocalTime,
                date: LocalDate, messageText: String) {
        this.sender = sender
        this.recipient = recipient
        this.time = time
        this.date = date
        this.messageText = messageText
    }
}