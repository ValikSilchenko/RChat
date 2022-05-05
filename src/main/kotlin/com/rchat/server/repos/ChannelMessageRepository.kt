package com.rchat.server.repos;

import com.rchat.server.models.ChannelMessage
import org.springframework.data.jpa.repository.JpaRepository

interface ChannelMessageRepository : JpaRepository<ChannelMessage, Int> {
}