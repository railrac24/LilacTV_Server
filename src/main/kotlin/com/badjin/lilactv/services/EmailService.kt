package com.badjin.lilactv.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

interface EmailService {
    fun sendEmail(email: SimpleMailMessage)
}

@Service("emailService")
class EmailServiceImpl : EmailService {

    @Autowired
    lateinit var mailSender: JavaMailSender

    @Async
    override fun sendEmail(email: SimpleMailMessage) {
        mailSender.send(email)
    }
}