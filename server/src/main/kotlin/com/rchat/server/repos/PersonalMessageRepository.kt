package com.rchat.server.repos

import com.rchat.server.models.PersonalMessage
import com.rchat.server.models.Users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PersonalMessageRepository: JpaRepository<PersonalMessage, Int> {
    @Query("select msg from PersonalMessage msg where msg.sender in (:user1, :user2)" +
            " and msg.recipient in (:user1, :user2) order by msg.date, msg.time")
    fun getChatMessages(user1: Users, user2: Users): List<PersonalMessage?>

    @Query("select msg from PersonalMessage msg" +
            " where (msg.sender = :forUser or msg.recipient = :forUser) and msg.date = (" +
            "select max(msg1.date) from PersonalMessage msg1 where msg1.sender in (msg.sender, msg.recipient)" +
            "and msg1.recipient in (msg.sender, msg.recipient)) and msg.time = (" +
            "select max(msg1.time) from PersonalMessage msg1 where msg1.sender in (msg.sender, msg.recipient)" +
            "and msg1.recipient in (msg.sender, msg.recipient) and msg1.date = msg.date)")
    fun getChats(forUser: Users): List<PersonalMessage?>

    @Query("select count(msg) - 0 as count from PersonalMessage msg" +
            " where msg.sender = :sender and msg.recipient = :recipient and msg.read = false")
    fun getUnreadCount(sender: Users, recipient: Users): Int
}