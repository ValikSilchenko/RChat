package com.rchat.server.repos

import com.rchat.server.models.PersonalMessage
import org.springframework.data.jpa.repository.JpaRepository

interface PersonalMessageRepository: JpaRepository<PersonalMessage, Int> {
}