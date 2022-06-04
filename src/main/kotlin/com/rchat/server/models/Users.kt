package com.rchat.server.models

import com.fasterxml.jackson.annotation.JsonView
import com.rchat.server.views.View
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "users")
open class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    constructor() {}

    constructor(username: String, email: String, phone: String, password: String) {
        this.username = username
        this.email = email
        this.phone = phone
        this.password = password
    }
}