package com.rchat.server.controllers

import com.rchat.server.models.ChannelMessage
import com.rchat.server.models.PersonalMessage
import com.rchat.server.models.PersonalRecipient
import com.rchat.server.models.User
import com.rchat.server.repos.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("message")
class ChatController(@Autowired private var personalMessageRepo: PersonalMessageRepository,
                     @Autowired private var channelMessageRepo: ChannelMessageRepository,
                     @Autowired private var userRepo: UserRepository,
                     @Autowired private var channelRepo: ChannelRepository,
                     @Autowired private var recipientRepo: PersonalRecipientRepository
) {
    @MessageMapping("/personal")
    fun processPersonal(@RequestBody message: PersonalMessage): PersonalMessage {
        return personalMessageRepo.save(message)
    }

    @MessageMapping("/channel")
    fun processChannel(@RequestBody message: ChannelMessage): ChannelMessage {
        return channelMessageRepo.save(message)
    }

    @GetMapping("personal")
    fun getAllPersonalMessages(sender: Int, recipient: Int): List<PersonalMessage> {
        return personalMessageRepo.getChatMessages(sender, recipient)
    }

    @PostMapping("personal")
    fun newPersonalMessage(message: PersonalMessage, recipient: PersonalRecipient) {
        personalMessageRepo.save(message)
        recipientRepo.save(recipient)
    }
}