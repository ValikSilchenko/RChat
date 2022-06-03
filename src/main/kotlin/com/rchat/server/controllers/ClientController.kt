package com.rchat.server.controllers

import com.rchat.server.models.*
import com.rchat.server.repos.ChannelRepository
import com.rchat.server.repos.MemberRepository
import com.rchat.server.repos.PersonalMessageRepository
import com.rchat.server.services.PgUserDetailsService

import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class ClientController(private var userService: PgUserDetailsService,
                       private var channelRepo: ChannelRepository,
                       private var memberRepo: MemberRepository,
                       private var personalMessageRepo: PersonalMessageRepository) {
//    @PostMapping("channel")
//    fun addChannel(@Valid channel: Channel, bindingResult: BindingResult): String {
//        if (bindingResult.hasErrors())
//            return "binding error"
//        channelRepo.save(channel)
//        return "success"
//    }

//    @GetMapping("channel")
//    fun getChannelMessages(@Valid channel: Channel, bindingResult: BindingResult): List<ChannelMessage>? {
//        if (bindingResult.hasErrors())
//            return null
//        return channelRepo.getMessages(channel)
//    }

//    @PostMapping("member")
//    fun addMember(@Valid member: Member, bindingResult: BindingResult): String {
//        if (bindingResult.hasErrors())
//            return "binding error"
//        memberRepo.save(member)
//        return "success"
//    }

    @GetMapping("personal")
    fun getPersonalMessages(sender: Int, recipient: Int): List<PersonalMessage> {  // TODO
        return personalMessageRepo.getChatMessages(sender, recipient)
    }

    @PostMapping("login")
    fun login(@RequestParam username: String, @RequestParam password: String): String {
        return userService.login(username, password)
    }

    @PostMapping("/user")
    fun addUser(@Valid user: Users, bindingResult: BindingResult): String {
        if (bindingResult.hasErrors())
            return "error:incorrect data"
        println("username: ${user.username}")
        if (!userService.saveUser(user)) {
            return "error:user already exists"
        }
        userService.autoLogin(user)
        return "success"  // TODO request body
    }
}