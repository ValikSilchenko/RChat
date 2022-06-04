package com.rchat.server.repos

import com.rchat.server.models.PersonalMessage
import com.rchat.server.models.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PersonalMessageRepository: JpaRepository<PersonalMessage, Int> {
    @Query("select msg from PersonalMessage msg where msg.sender in (:id1, :id2)" +
            " and msg.recipient in (:id1, :id2) order by msg.date, msg.time")
    fun getChatMessages(id1: Users, id2: Users): List<PersonalMessage?>
}