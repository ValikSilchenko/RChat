package com.rchat.server.controllers

import com.rchat.server.models.PersonalMessage
import com.rchat.server.repos.PersonalMessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("message")
class ChatController(@Autowired private var personalMessageRepo: PersonalMessageRepository) {
    @MessageMapping("/")
    fun processPersonalMsg(@Payload message: PersonalMessage): PersonalMessage {
        return personalMessageRepo.save(message)
    }
}