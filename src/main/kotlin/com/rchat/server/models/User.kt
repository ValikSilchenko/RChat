package com.rchat.server.models

import javax.persistence.*

@Entity
@Table(name = "users")
open class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Int? = null

    @Column(name = "username", nullable = false, length = 20)
    open var username: String? = null

    @Column(name = "email", nullable = false, length = 25)
    open var email: String? = null

    @Column(name = "phone", nullable = false, length = 10)
    open var phone: String? = null

    @Column(name = "password", nullable = false, length = 20)
    open var password: String? = null
}