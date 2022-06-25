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
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.DeleteMapping

@RestController
class ClientController(
    private var userService: PgUserDetailsService,
    private var channelRepo: ChannelRepository,
    private var memberRepo: MemberRepository,
    private var personalMessageRepo: PersonalMessageRepository
) {
//    @GetMapping("channel")
//    fun getChannelMessages(@Valid channel: Channel, bindingResult: BindingResult): List<ChannelMessage>? {
//        if (bindingResult.hasErrors())
//            return null
//        return channelRepo.getMessages(channel)
//    }

    @JsonView(View.Message::class)
    @GetMapping("/chats")
    fun getListOfChats(@RequestParam username: String): List<PersonalMessage?> {
        return personalMessageRepo.getChats(userService.getByName(username))
    }

    @GetMapping("/count")
    fun getUnreadCount(@RequestParam sender: String, @RequestParam recipient: String): Int {
        return personalMessageRepo.getUnreadCount(userService.getByName(sender), userService.getByName(recipient))
    }

    @GetMapping("/find")
    fun getListOfMatchUsers(@RequestParam username: String): List<String?> {
        return userService.getMatchUsers(username)
    }

    @JsonView(View.AllWithId::class)
    @GetMapping("/personal")
    fun getPersonalMessages(@RequestParam sender: String, @RequestParam recipient: String): List<PersonalMessage?> {
        return personalMessageRepo.getChatMessages(
            userService.getByName(sender),
            userService.getByName(recipient)
        )
    }

    @PostMapping("/login")
    fun login(@RequestParam username: String, @RequestParam password: String): ResponseEntity<String> {
        return userService.login(username, password)
    }

    @PostMapping("/user")
    fun addUser(@Valid user: Users, bindingResult: BindingResult): ResponseEntity<String> {
        if (bindingResult.hasErrors())
            return ResponseEntity("Неверные данные", HttpStatus.BAD_REQUEST)
        if (!userService.saveUser(user)) {
            return ResponseEntity("Пользователь с таким именем уже существует", HttpStatus.BAD_REQUEST)
        }
//        userService.autoLogin(user)
        return ResponseEntity<String>(HttpStatus.OK)
    }

    @PostMapping("/channel")
    fun addChannel(@RequestParam ownerId: String, @RequestParam channelName: String): ResponseEntity<String> {
        if (ownerId.toIntOrNull() == null)
            return ResponseEntity("Неверный формат для id", HttpStatus.BAD_REQUEST)

        val channel = Channel(userService.getById(ownerId.toInt()), channelName)
        channelRepo.save(channel)

        memberRepo.save(
            Member(
                MemberId(channel.id, ownerId.toInt()),
                channel,
                channel.owner
            )
        )
        return ResponseEntity<String>(HttpStatus.OK)
    }

//    @DeleteMapping("/channel")
//    fun delChannel(@RequestParam channelId,...): ResponseEntity<String> {}

    @PostMapping("/member")
    fun addMember(@RequestParam channelId: String, @RequestParam userId: String): ResponseEntity<String> {
        if (channelId.toIntOrNull() == null)
            return ResponseEntity("Неверный id канала", HttpStatus.BAD_REQUEST)
        if (userId.toIntOrNull() == null)
            return ResponseEntity("Неверный id пользователя", HttpStatus.BAD_REQUEST)

        val member = Member(
            MemberId(channelId.toInt(), userId.toInt()),
            channelRepo.getById(channelId.toInt()),
            userService.getById(userId.toInt())
        )

        if (memberRepo.findByIdOrNull(member.id) == null)
            memberRepo.save(member)
        return ResponseEntity<String>(HttpStatus.OK)
    }

    @DeleteMapping("/member")
    fun delMember(@RequestParam channelId: String, @RequestParam userId: String): ResponseEntity<String> {
        if (channelId.toIntOrNull() == null)
            return ResponseEntity("Неверный id канала", HttpStatus.BAD_REQUEST)
        if (userId.toIntOrNull() == null)
            return ResponseEntity("Неверный id пользователя", HttpStatus.BAD_REQUEST)

        val channel = channelRepo.getById(channelId.toInt())
        memberRepo.deleteById(MemberId(channelId.toInt(), userId.toInt()))

//        if (channel.owner?.id == userId.toInt()) {
//            channel.owner = memberRepo.findOne()
//            channelRepo.save(channel)
//        }
        return ResponseEntity<String>(HttpStatus.OK)
    }
}