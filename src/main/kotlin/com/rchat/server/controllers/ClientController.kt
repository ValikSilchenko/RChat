package com.rchat.server.controllers

import com.rchat.server.models.Channel
import com.rchat.server.models.Member
import com.rchat.server.models.User
import com.rchat.server.repos.ChannelRepository
import com.rchat.server.repos.MemberRepository
import com.rchat.server.repos.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ClientController(@Autowired private var userRepo: UserRepository,
                       @Autowired private var channelRepo: ChannelRepository,
                       @Autowired private var memberRepo: MemberRepository) {
    @PostMapping("/adduser")
    fun addUser(@RequestParam username: String, @RequestParam email: String,
                @RequestParam phone: String, @RequestParam password: String): String {
        val user = User(username, email, phone, password)
        userRepo.save(user)
        return "index"
    }

    @PostMapping("/addchannel")
    fun addChannel(channel: Channel): String {
        channelRepo.save(channel)
        return "index"
    }

    @PostMapping("/addmember")
    fun addMember(member: Member): String {
        memberRepo.save(member)
        return "index"
    }

    @GetMapping("/register")
    fun register(model: Model): String {
        return "form"
    }
}