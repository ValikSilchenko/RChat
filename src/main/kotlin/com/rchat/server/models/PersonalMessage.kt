package com.rchat.server.models

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import com.rchat.server.views.View
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.hibernate.annotations.Proxy
import org.hibernate.annotations.Type
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "personal_messages")
@Proxy(lazy = false)
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @Column(name = "\"time\"", nullable = false)
    open var time: LocalTime? = null

    @JsonView(View.Message::class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    @Column(name = "date", nullable = false)
    open var date: LocalDate? = null

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @JsonView(View.Message::class)
    @Column(name = "message_text", nullable = false)
    open var messageText: String? = null

    @JsonView(View.Message::class)
    @Column(name = "read", nullable = false)
    open var read: Boolean? = false
}