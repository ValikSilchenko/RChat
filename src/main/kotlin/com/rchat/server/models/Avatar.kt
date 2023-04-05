package com.rchat.server.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.Type
import javax.persistence.*


@Entity
@Table(name = "avatars")
open class Avatar {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    open var id: Int? = null

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonIgnoreProperties("hibernateLazyInitializer")
    @JoinColumn(name = "user_id", nullable = false)
    open var userId: Users? = null

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @Column(name = "img", nullable = false)
    open var img: ByteArray? = null

    constructor() {}

    constructor(user: Users, img: ByteArray) {
        this.userId = user
        this.img = img
    }
}