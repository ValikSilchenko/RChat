package com.rchat.server.controllers

import com.rchat.server.models.Channel
import com.rchat.server.models.Member
import com.rchat.server.models.PersonalMessage
import com.rchat.server.models.Users
import com.rchat.server.repos.ChannelRepository
import com.rchat.server.repos.MemberRepository
import com.rchat.server.repos.PersonalMessageRepository
import com.rchat.server.services.PgUserDetailsService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("client")
class ClientController(@Autowired private var userService: PgUserDetailsService,
                       @Autowired private var channelRepo: ChannelRepository,
                       @Autowired private var memberRepo: MemberRepository,
                       @Autowired private var personalMessageRepo: PersonalMessageRepository) {
    @PostMapping("channel")
    fun addChannel(channel: Channel): Channel {
        return channelRepo.save(channel)
    }

    @PostMapping("member")
    fun addMember(member: Member): Member {
        return memberRepo.save(member)
    }

    @GetMapping("personal")
    fun getPersonalMessages(sender: Int, recipient: Int): List<PersonalMessage> {  // TODO
        return personalMessageRepo.getChatMessages(sender, recipient)
    }

    @PostMapping("user")
    fun addUser(@Valid user: Users, bindingResult: BindingResult, model: Model): String {
        if (bindingResult.hasErrors())
            return "error"
        if (!userService.saveUser(user)) {
            return "error"
        }
        userService.autoLogin(user)
        return "success"  // TODO request body
    }
}