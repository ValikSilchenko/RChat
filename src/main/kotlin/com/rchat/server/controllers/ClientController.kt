package com.rchat.server.controllers

import com.rchat.server.models.*
import com.rchat.server.services.PgUserDetailsService
import com.rchat.server.views.View

import org.springframework.validation.BindingResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import javax.validation.Valid
import com.fasterxml.jackson.annotation.JsonView
import com.rchat.server.repos.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*
import javax.transaction.Transactional

@RestController
class ClientController(
    private var userService: PgUserDetailsService,
    private var avatarRepo: AttachmentRepository,
    private var channelRepo: ChannelRepository,
    private var channelMessageRepo: ChannelMessageRepository,
    private var memberRepo: MemberRepository,
    private var personalMessageRepo: PersonalMessageRepository
) {
    private var verification: MutableMap<String, String> = mutableMapOf()

    @JsonView(View.UserWithAvatar::class)
    @GetMapping("/chats")
    fun getListOfChats(@RequestParam userId: String): List<PersonalMessage?> {
        return personalMessageRepo.getChats(userService.getById(userId.toInt()))
    }

    @GetMapping("/count")
    fun getUnreadCount(@RequestParam sender: String, @RequestParam recipient: String): Int {
        return personalMessageRepo.getUnreadCount(userService.getByName(sender), userService.getByName(recipient))
    }

    @JsonView(View.UserWithId::class)
    @GetMapping("/find")
    fun getListOfMatchUsers(@RequestParam username: String): List<Users?> {
        return userService.getMatchUsers(username)
    }

    @JsonView(View.AllWithId::class)
    @GetMapping("/personal")
    fun getPersonalMessages(@RequestParam senderId: String, @RequestParam recipientId: String): List<PersonalMessage?> {
        return personalMessageRepo.getChatMessages(
            userService.getById(senderId.toInt()),
            userService.getById(recipientId.toInt())
        )
    }

    @Transactional
    @DeleteMapping("/personal")
    fun deleteChat(@RequestParam id1: String, @RequestParam id2: String): ResponseEntity<String> {
        personalMessageRepo.deleteChat(userService.getById(id1.toInt()), userService.getById(id2.toInt()))
        return ResponseEntity<String>(HttpStatus.OK)
    }

    @JsonView(View.UserWithAvatar::class)
    @PostMapping("/login")
    fun login(@RequestParam email: String, @RequestParam password: String): ResponseEntity<Users> {
        val user = userService.login(email, password) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PostMapping("/avatar")
    fun setAvatar(@RequestParam userId: String, @RequestParam img: ByteArray): ResponseEntity<String> {
        userService.updateAvatar(userId.toInt(), img)
        return ResponseEntity(HttpStatus.OK)
    }

    @JsonView(View.Avatar::class)
    @GetMapping("/avatar")
    fun getAvatar(@RequestParam userId: String): ResponseEntity<Any> {
        val user = userService.getById(userId.toInt())

        return ResponseEntity.ok(user)
    }

    @PostMapping("/register", params = ["username", "email", "phone", "password"])
    fun addUser(@Valid user: Users, bindingResult: BindingResult): ResponseEntity<Any> {
        if (bindingResult.hasErrors())
            return ResponseEntity("Not valid user data", HttpStatus.BAD_REQUEST)
        if (!userService.registerUser(user)) {  // save user by email
            return ResponseEntity("User with this email already exists", HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @JsonView(View.UserWithId::class)
    @PostMapping("/verify")
    fun verifyRegistration(@RequestParam userMail: String, @RequestParam verificationCode: String): ResponseEntity<Any> {
        val user: Users = userService.saveUser(userMail, verificationCode) ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PostMapping("/recover_password")
    fun recoverPassword(@RequestParam userMail: String): ResponseEntity<Any> {
        if (userService.recoverPassword(userMail))
            return ResponseEntity(HttpStatus.OK)
        return ResponseEntity("Email doesn't exists", HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/verify_recover")
    fun verifyRecover(@RequestParam userMail: String, @RequestParam verificationCode: String): ResponseEntity<Any> {
        if (userService.verifyRecover(userMail, verificationCode))
            return ResponseEntity(HttpStatus.OK)
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @PostMapping("/change_password")
    fun changePassword(@RequestParam userMail: String, @RequestParam newPassword: String): ResponseEntity<Any> {
        if (userService.changePassword(userMail, newPassword))
            return ResponseEntity(HttpStatus.OK)
        return ResponseEntity(HttpStatus.BAD_REQUEST)
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
}