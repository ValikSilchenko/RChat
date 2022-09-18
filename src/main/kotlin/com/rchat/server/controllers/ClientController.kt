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
import com.rchat.server.repos.ChannelMessageRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.DeleteMapping
import javax.transaction.Transactional

@RestController
class ClientController(
    private var userService: PgUserDetailsService,
    private var channelRepo: ChannelRepository,
    private var channelMessageRepo: ChannelMessageRepository,
    private var memberRepo: MemberRepository,
    private var personalMessageRepo: PersonalMessageRepository
) {
    @JsonView(View.UserWithId::class)
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

    @JsonView(View.MessageWithId::class)
    @GetMapping("/personal")
    fun getPersonalMessages(@RequestParam sender: String, @RequestParam recipient: String): List<PersonalMessage?> {
        return personalMessageRepo.getChatMessages(
            userService.getByName(sender),
            userService.getByName(recipient)
        )
    }

    @Transactional
    @DeleteMapping("/personal")
    fun deleteChat(@RequestParam id1: String, @RequestParam id2: String): ResponseEntity<String> {
        personalMessageRepo.deleteChat(userService.getById(id1.toInt()), userService.getById(id2.toInt()))
        return ResponseEntity<String>(HttpStatus.OK)
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
    fun addChannel(
        @RequestParam ownerId: String,
        @RequestParam channelName: String,
        @RequestParam(required = false) membersToAdd: String? = null
    ): ResponseEntity<String> { // TODO error handling
        if (ownerId.toIntOrNull() == null)
            return ResponseEntity("Неверный формат для id", HttpStatus.BAD_REQUEST)

        val owner = userService.getById(ownerId.toInt())
        val channel = Channel(owner, channelName)
        channelRepo.save(channel)

        memberRepo.save(
            Member(
                MemberId(channel.id, ownerId.toInt()),
                channel,
                owner,
                1
            )
        )

        if (membersToAdd != null) {
            try {
                val ids = membersToAdd.substring(1, membersToAdd.length - 1).split(", ")
                ids.forEach {
                    println(it)
                    memberRepo.save(
                        Member(
                            MemberId(channel.id, it.toInt()),
                            channel,
                            userService.getById(it.toInt()),
                            memberRepo.getMaxParticipatingNum(channel) + 1
                        )
                    )
                }
            } catch (error: Exception) {
                return ResponseEntity("usersToAdd: ${error.message}", HttpStatus.BAD_REQUEST)
            }
        }

        return ResponseEntity<String>("${channel.id}", HttpStatus.OK)
    }

//    @DeleteMapping("/channel")
//    fun delChannel(@RequestParam channelId,...): ResponseEntity<String> {}

    @PostMapping("/member")
    fun addMember(@RequestParam channelId: String, @RequestParam membersToAdd: String): ResponseEntity<String> {
        if (channelId.toIntOrNull() == null)
            return ResponseEntity("Неверный формат id канала", HttpStatus.BAD_REQUEST)

        val channel = channelRepo.getById(channelId.toInt())
        try {
            val ids = membersToAdd.substring(1, membersToAdd.length - 1).split(", ")
            ids.forEach {
                val member = Member(
                    MemberId(channel.id, it.toInt()),
                    channel,
                    userService.getById(it.toInt()),
                    memberRepo.getMaxParticipatingNum(channel) + 1
                )

                if (memberRepo.findByIdOrNull(member.id) == null)
                    memberRepo.save(member)
            }
        } catch (error: Exception) {
            return ResponseEntity("usersToAdd: ${error.message}", HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity<String>(HttpStatus.OK)
    }

    @DeleteMapping("/member")
    fun delMember(@RequestParam channelId: String, @RequestParam memberId: String): ResponseEntity<String> {
        if (channelId.toIntOrNull() == null)
            return ResponseEntity("Неверный формат id канала", HttpStatus.BAD_REQUEST)
        if (memberId.toIntOrNull() == null)
            return ResponseEntity("Неверный формат id пользователя", HttpStatus.BAD_REQUEST)

        val channel = channelRepo.getById(channelId.toInt())
        if (!memberRepo.existsById(MemberId(channelId.toInt(), memberId.toInt())))
            return ResponseEntity("Неверные данные", HttpStatus.BAD_REQUEST)
        memberRepo.deleteById(MemberId(channelId.toInt(), memberId.toInt()))

        if (channel.owner?.id == memberId.toInt()) {
            val newOwner = memberRepo.getFirstParticipated(channel)
            if (newOwner != null) {
                channel.owner = newOwner
                channelRepo.save(channel)
            } else
                channelRepo.delete(channel)
        }
        return ResponseEntity<String>(HttpStatus.OK)
    }

//    @JsonView(View.AllWithId::class)
//    @GetMapping("/channel")
//    fun getChannelMessages(@RequestParam channelId: String): List<ChannelMessage?> {
//        return channelMessageRepo.getMessages(channelRepo.getById(channelId.toInt()))  // TODO
//    }
}