package com.rchat.server.repos

import com.rchat.server.models.Avatar
import com.rchat.server.models.Users
import org.springframework.data.jpa.repository.JpaRepository

interface AvatarRepository : JpaRepository<Avatar, Int> {
    fun getByUserId(userId: Users): Avatar?
}