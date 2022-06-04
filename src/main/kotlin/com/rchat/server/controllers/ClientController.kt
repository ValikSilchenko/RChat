package com.rchat.server.controllers

import com.rchat.server.models.*
import com.rchat.server.repos.ChannelRepository
import com.rchat.server.repos.MemberRepository
import com.rchat.server.repos.PersonalMessageRepository
import com.rchat.server.services.PgUserDetailsService
import com.rchat.server.views.View

import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import javax.validation.Valid
import com.fasterxml.jackson.annotation.JsonView

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

    @GetMapping("/find")
    fun getListOfMatchUsers(@RequestParam username: String): List<String?> {
        return userService.getMatchUsers(username)
    }

    @JsonView(View.Message::class)
    @GetMapping("/personal")
    fun getPersonalMessages(@RequestParam user1: String, @RequestParam user2: String): List<PersonalMessage?> {
        return personalMessageRepo.getChatMessages(
            userService.getByName(user1),
            userService.getByName(user2)
        )
    }

    @PostMapping("/login")
    fun login(@RequestParam username: String, @RequestParam password: String): ResponseEntity<Int> {
        return userService.login(username, password)
    }

    @PostMapping("/user")
    fun addUser(@Valid user: Users, bindingResult: BindingResult): ResponseEntity<Int> {
        if (bindingResult.hasErrors())
            return ResponseEntity<Int>(HttpStatus.BAD_REQUEST)
        if (!userService.saveUser(user)) {
            return ResponseEntity<Int>(HttpStatus.BAD_REQUEST)
        }
//        userService.autoLogin(user)
        return ResponseEntity<Int>(HttpStatus.OK)
    }
}