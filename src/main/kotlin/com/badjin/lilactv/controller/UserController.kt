package com.badjin.lilactv.controller

import com.badjin.lilactv.model.Users
import com.badjin.lilactv.services.HttpSessionUtils
import com.badjin.lilactv.services.LilacTVServices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import java.lang.IllegalStateException
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/users")
class UserController {

    @Autowired
    lateinit var serviceModule: LilacTVServices

    @Autowired
    lateinit var loginSession: HttpSessionUtils

    @GetMapping("/login")
    fun login(): String {
        return "login"
    }

    @PostMapping("/login")
    fun postLogin(session: HttpSession, model: Model,
                  @RequestParam(value = "email") email: String,
                  @RequestParam(value = "pass") password: String): String {

        try {
            serviceModule.loginProcess(session, email, password)
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "redirect:/index"

    }

    @GetMapping("/logout")
    fun logout(session: HttpSession): String {
        session.removeAttribute("session_user")
        session.removeAttribute("admin")
        session.removeAttribute("lilactvUser")
        return "redirect:/index"
    }

    @GetMapping("/register")
    fun register(): String {
        return "register"
    }

    @PostMapping("/register")
    fun register(model: Model,
                 @RequestParam(value = "name") name: String,
                 @RequestParam(value = "email") email: String,
                 @RequestParam(value = "mobile") mobile: String,
                 @RequestParam(value = "pass") password: String,
                 @RequestParam(value = "cpass") cpassword: String
                 ): String {

        val user = Users(name, email, mobile, cpassword)
        try {
            serviceModule.registerProcess(user)
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "register"
        }
        return "redirect:/users/login"
    }

    @GetMapping("/{id}/form")
    fun updateUserData(model: Model, session: HttpSession, @PathVariable id: Long): String {

        try {
            if (loginSession.hasPermission(session,id)) {
                val (user, checked, productID) = serviceModule.getSelectedUser4Edit(id)
                model["lilactv"] = checked
                model["lilactvID"] = productID
                model["user"] = user
                if (id == 1L) return "redirect:/admin/userList"
            }
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }

        return "updateUser"
    }

    @GetMapping("/updateUser")
    fun updateUser(): String {
        return "updateUser"
    }

    @PostMapping("/updateUser")
    fun update(session: HttpSession, model: Model,
                 @RequestParam(value = "name") name: String,
                 @RequestParam(value = "email") email: String,
                 @RequestParam(value = "mobile") mobile: String,
                 @RequestParam(value = "lilactvID") lilactvID: String,
                 @RequestParam(value = "pass") password: String,
                 @RequestParam(value = "cpass") cpassword: String
                 ): String {
        try {
            serviceModule.updateUserInfo(Users(name, email, mobile, password), lilactvID)

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            val (user, checked, productID) = serviceModule.getSelectedUser4Edit(email)
            model["lilactv"] = checked
            model["lilactvID"] = productID
            if (user != null) {
                model["user"] = user
            }
            return "updateUser"
        }
        return "redirect:/index"
    }

    @GetMapping("/{id}/resetPassword")
    fun resetPasswordPage(model: Model,
                          session: HttpSession,
                          @PathVariable id: Long): String {

        try {
            loginSession.hasPermission(session,id)
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }

        return "reset-password"
    }

    @PostMapping("/resetPassword")
    fun setNewPassword(model: Model,
                       session: HttpSession,
                       @RequestParam(value = "pass") password: String): String {

        try {
            val user = loginSession.getUserFromSession(session) as Users
            user.password = serviceModule.util.crypto(password)
            serviceModule.saveUser(user)
            model["colorMsg"] = true
            model["errorMsg"] = "비밀번호를 성공적으로 변경했습니다."

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "login"
    }

    @GetMapping("/{id}/activate")
    fun showLilacTVStatus(model: Model,
                          session: HttpSession,
                          @PathVariable id: Long): String {

        try {
            if (loginSession.hasPermission(session,id)) {
                val loginUser = loginSession.getUserFromSession(session) as Users
                val subscription = serviceModule.getSubscription(id)
                if (subscription.status.state != "Wait") {
                    val (user, checked, productID) = serviceModule.getSelectedUser4Edit(id)
                    model["lilactvID"] = productID
                }
                model["subscription"] = subscription
                model["username"] = loginUser.name
            }
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }

        return "activateLilacTV"
    }

    @PostMapping("/activate")
    fun activateLilacTV(model: Model,
                        session: HttpSession,
                        @RequestParam(value = "lilactvID") lilactvID: String): String {

        val user = loginSession.getUserFromSession(session) as Users
        try {
            val subscription = serviceModule.getSubscription(user.id!!)
            user.password = ""
            serviceModule.updateUserInfo(user, lilactvID)
            serviceModule.setSubscription(subscription, user.id!!)
            session.setAttribute("lilactvUser", true)

        } catch (e: IllegalStateException) {

            model["errorMsg"] = e.message!!
            return "activateLilacTV"

        }
        return "redirect:/users/${user.id}/activate"
    }

    @GetMapping("/firmware")
    fun firmwarePage(model: Model,
                     session: HttpSession): String {
        try {
            loginSession.hasLilacTV(session)
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "firmware"
    }
}