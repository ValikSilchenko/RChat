package com.rchat.server.controllers

import com.rchat.server.models.ChannelMessage
import com.rchat.server.models.PersonalMessage
import com.rchat.server.models.PersonalRecipient
import com.rchat.server.repos.ChannelMessageRepository
import com.rchat.server.repos.PersonalMessageRepository
import com.rchat.server.repos.PersonalRecipientRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDate
import java.time.LocalTime
import javax.validation.Valid

@Controller
@RequestMapping("message")
class ChatController(@Autowired private var personalMessageRepo: PersonalMessageRepository,
                     @Autowired private var channelMessageRepo: ChannelMessageRepository,
                     @Autowired private var recipientRepo: PersonalRecipientRepository) {
    @MessageMapping("/personal")
    @SendTo("/chatTopic")
    fun processPersonal(@Valid message: PersonalMessage, bindingResult1: BindingResult,
                        @Valid recipient: PersonalRecipient, bindingResult2: BindingResult): String {
        if (bindingResult1.hasErrors() || bindingResult2.hasErrors())
            return "binding error"
        message.date = LocalDate.now()
        message.time = LocalTime.now()
        personalMessageRepo.save(message)
        recipientRepo.save(recipient)
        return "success"
    }

    @MessageMapping("/channel")
    @SendTo("/channelTopic")
    fun processChannel(@Valid message: ChannelMessage, bindingResult: BindingResult): ChannelMessage? {
        if (bindingResult.hasErrors())
            return null
        message.date = LocalDate.now()
        message.time = LocalTime.now()
        return channelMessageRepo.save(message)
    }
}