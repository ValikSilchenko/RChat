package com.rchat.server.controllers

import com.fasterxml.jackson.annotation.JsonView
import com.rchat.server.models.ChannelMessage
import com.rchat.server.models.PersonalMessage
import com.rchat.server.repos.ChannelMessageRepository
import com.rchat.server.repos.PersonalMessageRepository
import com.rchat.server.services.PgUserDetailsService
import com.rchat.server.views.View

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.time.LocalDate
import java.time.LocalTime

@Controller
class ChatController(private var personalMessageRepo: PersonalMessageRepository,
                     private var channelMessageRepo: ChannelMessageRepository,
                     private var userService: PgUserDetailsService) {
    @JsonView(View.AllWithId::class)
    @MessageMapping("/user/{recipient}/")
    @SendTo("/chatTopic/{recipient}/")
    fun processPersonal(@DestinationVariable recipient: String,  msg: String): PersonalMessage {
        val data = parseMessage(msg)  // data[0] - sender, data[1] - message
        val message = PersonalMessage(userService.getByName(data[0]),
            userService.getByName(recipient),
            LocalTime.now(),
            LocalDate.now(),
            data[1])
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

    @MessageMapping("/test/{recipientId}/")
    @SendTo("/chatTopic/")
    fun test(@DestinationVariable recipientId: String,  msg: String): String {
        println("Message: $msg; Sender: $recipientId")
        return msg
    }

    fun parseMessage(msg: String): List<String> {
        return listOf(msg.substringBefore(' '), msg.substringAfter(' '))
    }
}