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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import java.time.LocalDate
import java.time.LocalTime
import javax.transaction.Transactional

@Controller
class ChatController(
    private var personalMessageRepo: PersonalMessageRepository,
    private var channelMessageRepo: ChannelMessageRepository,
    private var userService: PgUserDetailsService
) {
    @Transactional
    @JsonView(View.AllWithId::class)
    @MessageMapping("/user/{recipient}/{sender}/")
    @SendTo("/chatTopic/{recipient}/", "/chatTopic/{sender}/")
    fun processPersonal(
        @DestinationVariable recipient: String,
        @DestinationVariable sender: String,
        msg: String
    ): PersonalMessage {
        val date = LocalDate.now()
        val time = LocalTime.now()
        val message = PersonalMessage(
            userService.getByName(sender),
            userService.getByName(recipient),
            time,
            date,
            msg
        )
        personalMessageRepo.save(message)
        message.sender?.let { msgSender ->
            message.recipient?.let { msgRecipient ->
                personalMessageRepo.updateReadBefore(msgSender, msgRecipient, date, time)
            }
        }
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

    @JsonView(View.AllWithId::class)  // возвращать только id и read
    @MessageMapping("/message/{recipient}/{sender}/{msgId}/")
    @SendTo("/chatTopic/{recipient}/", "/chatTopic/{sender}/")
    fun updateRead(
        @DestinationVariable recipient: String,
        @DestinationVariable sender: String,
        @DestinationVariable msgId: String
    ): PersonalMessage {  // прочитать
        val message = personalMessageRepo.getById(msgId.toInt())
        message.read = true
        personalMessageRepo.save(message)
        return message
    }

    @Transactional
    @MessageMapping("/delete/{user1}/{user2}/")
    @SendTo("chatTopic/{user1}/", "chatTopic/{user2}/")
    fun deleteMessage(
        @DestinationVariable user1: String,
        @DestinationVariable user2: String,
        msgId: String
    ): Map<String, String> {
        personalMessageRepo.deletePersonalMessageById(msgId.toInt())
        return mapOf("deleted" to msgId)
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