package com.rchat.server.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import java.nio.charset.StandardCharsets
import javax.mail.MessagingException


@Service
class DefaultEmailService {
    @Autowired
    private val emailSender: JavaMailSender? = null

    @Autowired
    private val templateEngine: SpringTemplateEngine? = null

    @Throws(MessagingException::class)
    fun sendMail(context: EmailContext) {
        val message = emailSender!!.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(
            message,
            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name()
        )

        mimeMessageHelper.setFrom(context.from)
        mimeMessageHelper.setTo(context.to)
        mimeMessageHelper.setSubject(context.subject)
        mimeMessageHelper.setText(context.body, true)
        emailSender.send(message)
    }
}