package com.rchat.server.controllers

import com.rchat.server.models.PersonalMessage
import com.rchat.server.repos.ChannelMessageRepository
import com.rchat.server.repos.PersonalMessageRepository
import com.rchat.server.services.PgUserDetailsService
import com.rchat.server.views.View

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import com.fasterxml.jackson.annotation.JsonView
import org.springframework.messaging.handler.annotation.Header
import java.time.LocalDate
import java.time.LocalTime

@Controller
class ChatController(private var personalMessageRepo: PersonalMessageRepository,
                     private var channelMessageRepo: ChannelMessageRepository,
                     private var userService: PgUserDetailsService) {
    @JsonView(View.AllWithId::class)
    @MessageMapping("/user/{recipient}/{sender}/")
    @SendTo("/chatTopic/{recipient}/", "/chatTopic/{sender}/")
    fun processPersonal(
        @DestinationVariable recipient: String,
        @DestinationVariable sender: String,
        msg: String): PersonalMessage {
        val message = PersonalMessage(userService.getByName(sender),
            userService.getByName(recipient),
            LocalTime.now(),
            LocalDate.now(),
            msg)
        personalMessageRepo.save(message)
        return message
    }

    @JsonView(View.AllWithId::class)
    @MessageMapping("/user/{recipient}/{sender}/{msgId}/")
    @SendTo("/chatTopic/{recipient}/", "/chatTopic/{sender}/")
    fun updatePersonal(
        @DestinationVariable recipient: String,
        @DestinationVariable sender: String,
        @DestinationVariable msgId: String,
        newMsg: String
    ): PersonalMessage {
        val message = personalMessageRepo.getById(msgId.toInt())
        message.messageText = newMsg
        personalMessageRepo.save(message)
        return message
    }

    @JsonView(View.AllWithId::class)
    @MessageMapping("/message/{recipient}/{sender}/{msgId}/")
    @SendTo("/chatTopic/{recipient}/", "/chatTopic/{sender}/")
    fun updateRead(
        @DestinationVariable recipient: String,
        @DestinationVariable sender: String,
        @DestinationVariable msgId: String
    ): PersonalMessage {
        val message = personalMessageRepo.getById(msgId.toInt())
        message.read = true
        personalMessageRepo.save(message)
        return message
    }

//    @MessageMapping("/channel")
//    @SendTo("/channelTopic")
//    fun processChannel(@Valid message: ChannelMessage, bindingResult: BindingResult): ChannelMessage? {
//        if (bindingResult.hasErrors())
//            return null
//        message.date = LocalDate.now()
//        message.time = LocalTime.now()
//        return channelMessageRepo.save(message)
//    }
}