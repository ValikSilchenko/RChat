package com.rchat.server.controllers

import com.rchat.server.models.Channel
import com.rchat.server.models.Member
import com.rchat.server.models.User
import com.rchat.server.repos.ChannelRepository
import com.rchat.server.repos.MemberRepository
import com.rchat.server.repos.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping

@Controller
class ClientController(@Autowired private var userRepo: UserRepository,
                       @Autowired private var channelRepo: ChannelRepository,
                       @Autowired private var memberRepo: MemberRepository) {
    @PostMapping("/adduser")
    fun addUser(user: User): User {
        return userRepo.save(user)
    }

    @PostMapping("/addchannel")
    fun addChannel(channel: Channel): Channel {
        return channelRepo.save(channel)
    }

    @PostMapping("/addmember")
    fun addMember(member: Member): Member {
        return memberRepo.save(member)
    }
}