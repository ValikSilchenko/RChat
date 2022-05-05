package com.rchat.server.repos;

import com.rchat.server.models.Channel
import org.springframework.data.jpa.repository.JpaRepository

interface ChannelRepository : JpaRepository<Channel, Int> {
}