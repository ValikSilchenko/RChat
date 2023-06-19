package com.rchat.server.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring5.SpringTemplateEngine
import java.nio.charset.StandardCharsets
import javax.mail.MessagingException

import kotlin.random.Random


@Service
class DefaultEmailService {
    @Autowired
    private val emailSender: JavaMailSender? = null

    @Autowired
    private val templateEngine: SpringTemplateEngine? = null

    @Throws(MessagingException::class)
    fun sendMail(email: String): String {
        val message = emailSender!!.createMimeMessage()
        val mimeMessageHelper = MimeMessageHelper(
            message,
            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
            StandardCharsets.UTF_8.name()
        )
//        val context = Context()
//        context.setVariables(email.getContext())
//        val emailContent: String = templateEngine.process(email.getTemplateLocation(), context)
        mimeMessageHelper.setTo(email)
        mimeMessageHelper.setSubject("Подтверждение регистрации")
        var code = Random.nextInt(100000, 999999).toString()
        mimeMessageHelper.setText("<a>Ваш код подтверждения регистрации:</a> <h2>${code}</h2> <a>Введите его в приложении для подтверждения</a>", true)
        mimeMessageHelper.setFrom("no-reply.rchat@gmail.com")
        emailSender.send(message)
        return code
    }
}