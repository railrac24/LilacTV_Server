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
                 @RequestParam(value = "lilactvID") lilactvID: String,
                 @RequestParam(value = "pass") password: String,
                 @RequestParam(value = "cpass") cpassword: String
                 ): String {

        val user = Users(name, email, mobile, cpassword)
        try {
            serviceModule.registerProcess(user, lilactvID)
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "register"
        }
        return "redirect:/users/login"
    }

    @GetMapping("/{id}/form")
    fun updateUserData(model: Model, session: HttpSession, response: HttpServletResponse, @PathVariable id: Long): String {

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
        return if (session.getAttribute("admin") as Boolean) "redirect:/admin/userList" else "redirect:/index"
    }
}