package com.rchat.server.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.hibernate.annotations.Type
import javax.persistence.*
import com.rchat.server.views.View


@Entity
@Table(name = "attachment")
@NoArgsConstructor
@AllArgsConstructor
open class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    open var id: Int? = null

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @JsonView(View.Message::class)
    @Column(name = "img", nullable = false)
    open var img: ByteArray? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties("hibernateLazyInitializer")
    @JoinColumn(name = "msg_id")
    open var message: PersonalMessage? = null
}