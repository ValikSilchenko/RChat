package com.rchat.server.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.rchat.server.serializers.BytesToStringSerializer
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.hibernate.annotations.Type
import javax.persistence.*
import com.rchat.server.views.View
import javax.validation.constraints.Size


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
    @JsonSerialize(using = BytesToStringSerializer::class)
    @JsonView(View.Message::class)
    @Column(name = "img", nullable = false)
    @Size(max = 102400 * 1024)  // max size 100 MB
    open var img: ByteArray? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties("hibernateLazyInitializer")
    @JoinColumn(name = "msg_id")
    open var message: PersonalMessage? = null
}