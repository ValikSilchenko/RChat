package com.rchat.server.repos

import com.rchat.server.models.PersonalMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PersonalMessageRepository: JpaRepository<PersonalMessage, Int> {
    @Query("select msg from PersonalMessage msg where msg.sender = :id1 or msg.sender = :id2" +
            " order by msg.date, msg.time")
    fun getChatMessages(id1: Int, id2: Int): List<PersonalMessage>
}