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

    @Column(name = "email", nullable = false, length = 25)  // TODO length of email
    open var email: String? = null

    @Column(name = "phone", nullable = false, length = 10)
    open var phone: String? = null

    @Column(name = "password", nullable = false, length = 20)  // TODO length of pswrd
    open var password: String? = null

    constructor() {}

    constructor(id: Int, username: String, email: String, phone: String, password: String) {
        this.id = id
        this.username = username
        this.email = email
        this.phone = phone
        this.password = password
    }
}