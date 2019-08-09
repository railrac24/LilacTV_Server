package com.badjin.lilactv.controller

import com.badjin.lilactv.services.EmailService
import com.badjin.lilactv.services.LilacTVServices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.lang.IllegalStateException

@Controller
class Contact {

    @Autowired
    lateinit var serviceModule: LilacTVServices

    @Autowired
    lateinit var emailService: EmailService

    @GetMapping("/contact")
    fun contactPage(): String {
        return "contact"
    }

    @PostMapping("/contact")
    fun sendMessage(model: Model,
                    @RequestParam("name") name: String,
                    @RequestParam("email") userEmail: String,
                    @RequestParam("phone") phone: String,
                    @RequestParam("message") message: String
                    ): String {

        try {
            // Email message
            val customerEmail = SimpleMailMessage()
            customerEmail.setFrom("support@lilactv.com")
            customerEmail.setTo("railrac23@gmail.com")
            customerEmail.setSubject("${name}님이 보내신 의견입니다.")
            customerEmail.setText("$phone\n$userEmail\n\n$message")

            emailService.sendEmail(customerEmail)

            model["colorMsg"] = true
            model["errorMsg"] = "내용을 성공적으로 전송했습니다."
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "login"
    }

}