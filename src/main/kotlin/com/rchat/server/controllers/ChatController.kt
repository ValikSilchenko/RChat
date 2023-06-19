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
import org.springframework.messaging.Message
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException
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
    @MessageMapping("/user/{recipientID}/{senderID}/")
    @SendTo("/chatTopic/{recipientID}/", "/chatTopic/{senderID}/")
    fun processPersonal(
        @DestinationVariable recipientID: String,
        @DestinationVariable senderID: String,
        msg: String
    ): PersonalMessage {
        val date = LocalDate.now()
        val time = LocalTime.now()
        val message = PersonalMessage(
            userService.getById(senderID.toInt()),
            userService.getById(recipientID.toInt()),
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
    @MessageMapping("/user/{recipientID}/{senderID}/{msgID}/")
    @SendTo("/chatTopic/{recipientID}/", "/chatTopic/{senderID}/")
    fun updatePersonal(
        @DestinationVariable recipientID: String,
        @DestinationVariable senderID: String,
        @DestinationVariable msgID: String,
        newMsg: String
    ): PersonalMessage {
        val message = personalMessageRepo.getById(msgID.toInt())
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
    @MessageMapping("/delete/{recipient}/{sender}/")
    @SendTo("/chatTopic/{recipient}/", "/chatTopic/{sender}/")
    fun deleteMessage(
        @DestinationVariable recipient: String,
        @DestinationVariable sender: String,
        msgId: String
    ): Map<String, String> {
        val message = personalMessageRepo.getById(msgId.toInt())
        if (message.recipient == userService.getByName(recipient) && message.sender == userService.getByName(sender))
            return mapOf("error" to "access denied")
        personalMessageRepo.deletePersonalMessageById(msgId.toInt())
        return mapOf("deleted" to msgId)
    }
}