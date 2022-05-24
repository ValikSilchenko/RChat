package com.rchat.server.repos;

import com.rchat.server.models.Channel
import com.rchat.server.models.ChannelMessage
import com.rchat.server.models.Users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChannelRepository : JpaRepository<Channel, Int> {
    @Query("select mbr.user from Member mbr where mbr.channel = :chanel")
    fun getMembers(channel: Channel): List<Users>

    @Query("select msg from ChannelMessage msg where msg.channel = :channel")
    fun getMessages(channel: Channel): List<ChannelMessage>
}