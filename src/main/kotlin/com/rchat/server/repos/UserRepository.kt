package com.rchat.server.repos

import com.rchat.server.models.Users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository: JpaRepository<Users, Int> {
    fun findByUsername(username: String?): Users?
    fun findByEmail(email: String?): Users?

    @Query("select usr from Users usr where lower(usr.username) like :substr%")
    fun findMatchUsers(substr: String): List<Users?>
}