package com.rchat.server.controllers

import com.rchat.server.models.ChannelMessage
import com.rchat.server.models.PersonalMessage
import com.rchat.server.repos.ChannelMessageRepository
import com.rchat.server.repos.PersonalMessageRepository
import com.rchat.server.repos.UserRepository

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.time.LocalDate
import java.time.LocalTime

@Controller
class ChatController(private var personalMessageRepo: PersonalMessageRepository,
                     private var channelMessageRepo: ChannelMessageRepository,
                     private var userRepo: UserRepository) {
    @MessageMapping("/user/{recipientId}")
    @SendTo("/chatTopic/{recipientId}")
    fun processPersonal(@DestinationVariable recipientId: String,  msg: String): String {
        val data = parse(msg)  // data[0] - senderId, data[1] - message
        val message = PersonalMessage(userRepo.getById(data[0].toInt()),
            userRepo.getById(recipientId.toInt()),
            LocalTime.now(),
            LocalDate.now(),
            msg)
        personalMessageRepo.save(message)
        return data[1]
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

    @MessageMapping("/test/{id1}/{id2}")
    @SendTo("/chatTopic/{id1}{id2}")  // TODO
    fun test(@DestinationVariable id1: String, @DestinationVariable id2: String,  msg: String): String {
        println("Message: $msg; Sender: $id1")
        return msg
    }

    fun parse(msg: String): List<String> {
        return listOf(msg.substring(0, msg.indexOf(" ")),
            msg.substring(msg.indexOf(" ") + 1))
    }
}