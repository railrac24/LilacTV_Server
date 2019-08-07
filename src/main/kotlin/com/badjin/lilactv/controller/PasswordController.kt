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
import java.util.*
import javax.servlet.http.HttpServletRequest

@Controller
class PasswordController {

    @Autowired
    lateinit var serviceModule: LilacTVServices

    @Autowired
    lateinit var emailService: EmailService

    @GetMapping("/users/forgot")
    fun displayForgotPasswordPage(): String {
        return "forgot-password"
    }

    @PostMapping("/forgot")
    fun processForgotPasswordForm(model: Model,
                                  @RequestParam("email") userMail: String,
                                  request: HttpServletRequest): String {
        try {
            val user = serviceModule.isRegisteredEmail(userMail)
            user.resetToken = UUID.randomUUID().toString()
            serviceModule.saveUser(user)

            val appUrl = request.scheme + "://" + request.serverName

            // Email message
            val passwordResetEmail = SimpleMailMessage()
            passwordResetEmail.setFrom("railrac24@gmail.com")
            passwordResetEmail.setTo(user.email)
            passwordResetEmail.setSubject("비밀번호 재설정 안내")
            passwordResetEmail.setText("비밀번호를 재설정 하시려면 아래의 링크를 클릭하세요.\n\n$appUrl/reset?token=${user.resetToken}")

            emailService.sendEmail(passwordResetEmail)
            model["colorMsg"] = true
            model["errorMsg"] = "비밀번호 재설정 링크를 ${userMail}로 전송했습니다."
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "login"
    }

    @GetMapping("/reset")
    fun displayResetPasswordPage(model: Model, @RequestParam("token") token: String): String {

        try {
            serviceModule.isRegisteredToken(token)
            model["resetToken"] = token

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "reset-password"
    }

    @PostMapping("/reset")
    fun setNewPassword(model: Model,
                       @RequestParam("token") token: String,
                       @RequestParam("pass") pass: String): String {

        try {
            val user = serviceModule.isRegisteredToken(token)
            user.password = serviceModule.util.crypto(pass)
            user.resetToken = null
            serviceModule.saveUser(user)
            model["colorMsg"] = true
            model["errorMsg"] = "비밀번호를 성공적으로 변경했습니다."

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "login"
    }
}