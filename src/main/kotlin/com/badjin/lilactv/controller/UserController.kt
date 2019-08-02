package com.badjin.lilactv.controller

import com.badjin.lilactv.model.Users
import com.badjin.lilactv.repository.UserRepo
import com.badjin.lilactv.services.HttpSessionUtils
import com.badjin.lilactv.services.LilacTVServices
import com.badjin.lilactv.services.Utils
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
    lateinit var mysession: HttpSessionUtils

    @Autowired
    lateinit var userDB: UserRepo

    @GetMapping("/login")
    fun login(): String {
        return "/users/login"
    }

    @PostMapping("/login")
    fun postLogin(session: HttpSession, model: Model,
                  @RequestParam(value = "email") email: String,
                  @RequestParam(value = "pass") password: String): String {

        try {
            serviceModule.loginProcess(session, email, password)
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "/users/login"
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
        return "/users/register"
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
            return "/users/register"
        }
        return "redirect:/users/login"
    }

    @GetMapping("/{id}/form")
    fun updateUserData(model: Model, session: HttpSession, response: HttpServletResponse, @PathVariable id: Long): String {

        try {
            if (mysession.hasPermission(session, userDB.getOne(id))) {
                val (user, checked, productID) = serviceModule.getSelectedUser4Edit(id)
                model["lilactv"] = checked
                model["lilactvID"] = productID
                model["user"] = user
                if (id == 1L) return "redirect:/admin/userList"
            }
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "/users/login2"
        }

        return "/users/updateUser"
    }

    @GetMapping("/updateUser")
    fun updateUser(): String {
        return "/users/updateUser"
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
            val (user, checked, productID) = serviceModule.getSelectedUser4Edit(userDB.findByEmail(email)?.id!!)
            model["lilactv"] = checked
            model["lilactvID"] = productID
            model["user"] = user
            return "/users/updateUser"
        }
        return if (session.getAttribute("admin") as Boolean) "redirect:/admin/userList" else "redirect:/index"
    }
}