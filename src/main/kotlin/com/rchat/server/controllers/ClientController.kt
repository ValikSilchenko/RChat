package com.rchat.server.controllers

import com.rchat.server.models.Channel
import com.rchat.server.models.Member
import com.rchat.server.models.PersonalMessage
import com.rchat.server.models.Users
import com.rchat.server.repos.ChannelRepository
import com.rchat.server.repos.MemberRepository
import com.rchat.server.repos.PersonalMessageRepository
import com.rchat.server.repos.UserRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientController(@Autowired private var userRepo: UserRepository,
                       @Autowired private var channelRepo: ChannelRepository,
                       @Autowired private var memberRepo: MemberRepository,
                       @Autowired private var personalMessageRepo: PersonalMessageRepository) {
    @PostMapping("/adduser")
    fun addUser(user: Users): Users {
        println("${user.username}")
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

    @GetMapping("/personal")
    fun getPersonalMessages(sender: Int, recipient: Int): List<PersonalMessage> {  // TODO
        return personalMessageRepo.getChatMessages(sender, recipient)
    }

//    @GetMapping("/test")
//    fun test(): User {
////        val test = userRepo.getById(7)
//        val test2 = User("Memm", "abc@bbs.sru", "9532737230", "79dfg23")
//        return test2
//    }
}