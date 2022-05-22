package com.rchat.server.controllers

import com.rchat.server.models.ChannelMessage
import com.rchat.server.models.PersonalMessage
import com.rchat.server.repos.ChannelMessageRepository
import com.rchat.server.repos.PersonalMessageRepository
import com.rchat.server.repos.PersonalRecipientRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("message")
class ChatController(@Autowired private var personalMessageRepo: PersonalMessageRepository,
                     @Autowired private var channelMessageRepo: ChannelMessageRepository,
                     @Autowired private var recipientRepo: PersonalRecipientRepository) {
    @MessageMapping("/personal")
    fun processPersonal(@RequestBody message: PersonalMessage): PersonalMessage {
        return personalMessageRepo.save(message)
    }

    @MessageMapping("/channel")
    fun processChannel(@RequestBody message: ChannelMessage): ChannelMessage {
        return channelMessageRepo.save(message)
    }
}