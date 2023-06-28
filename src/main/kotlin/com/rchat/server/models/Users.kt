package com.rchat.server.models

import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.rchat.server.serializers.BytesToStringSerializer
import com.rchat.server.views.View
import org.hibernate.annotations.Proxy
import org.hibernate.annotations.Type
import javax.persistence.*
import javax.validation.constraints.Size


@Entity
@Table(name = "users")
@Proxy(lazy = false)
open class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(View.UserWithId::class)
    @Column(name = "id", nullable = false)
    open var id: Int? = null

    @JsonView(View.Message::class)
    @Column(name = "username", nullable = false, length = 20)
    @Size(min = 2, max = 20)
    open var username: String? = null

    @Column(name = "email", nullable = false, length = 60)
    @Size(min = 8, max = 60)
    open var email: String? = null

    @Column(name = "phone", nullable = false, length = 10)
    @Size(min = 10, max = 10)
    open var phone: String? = null

    @Column(name = "password", nullable = false, length = 60)
    @Size(min = 5, max = 60)
    open var password: String? = null

    @Lob
    @Type(type="org.hibernate.type.BinaryType")
    @JsonSerialize(using = BytesToStringSerializer::class)
    @JsonView(View.Avatar::class)
    @Column(name = "avatar", nullable = true)
    @Size(max = 3_145_728)  // max 3MB
    var avatar: ByteArray? = null
}