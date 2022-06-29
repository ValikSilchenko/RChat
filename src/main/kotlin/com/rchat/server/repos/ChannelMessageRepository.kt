package com.rchat.server.repos;

import com.rchat.server.models.Channel
import com.rchat.server.models.ChannelMessage

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChannelMessageRepository : JpaRepository<ChannelMessage, Int> {
    @Query("select msg from ChannelMessage msg where msg.channel = :channel")
    fun getMessages(channel: Channel): List<ChannelMessage?>
}