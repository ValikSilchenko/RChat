package com.rchat.server.repos

import com.rchat.server.models.Users

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<Users, Int> {
    fun findByUsername(username: String?): Users?
}