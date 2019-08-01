package com.badjin.lilactv.controller

import com.badjin.lilactv.model.Users
import com.badjin.lilactv.services.HttpSessionUtils
import com.badjin.lilactv.services.LilacTVServices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Controller
class UserController {

    @Autowired
    lateinit var serviceModule: LilacTVServices

    @Autowired
    lateinit var mysession: HttpSessionUtils


    @GetMapping("/users")
    fun users(model: Model, session: HttpSession): String {
        if (!mysession.isLoginUser(session)) return "login"
        if (!(session.getAttribute("admin") as Boolean)) throw IllegalAccessException("잘못된 접근입니다.")

        model["owner"] = serviceModule.getUserList()!!
        return "users"
    }

    @GetMapping("/{id}/form")
    fun updateUserData(model: Model, session: HttpSession, @PathVariable id: Long): String {
        if (!mysession.isLoginUser(session)) return "login"
        val sessionUser = mysession.getUserFromSession(session)
        if (sessionUser != null) {
            if (id != sessionUser.id){
                if (!(session.getAttribute("admin") as Boolean)) throw IllegalAccessException("잘못된 접근입니다.")
            }
        }

        if (id == 1L) {
            return "redirect:/users"
        }

        val (user, checked, productID) = serviceModule.getSelectedUser4Edit(id)
        model["lilactv"] = checked
        model["lilactvID"] = productID
        model["user"] = user
        return "updateUser"
    }

    @GetMapping("/{id}/delete")
    fun deleteSelected(session: HttpSession, @PathVariable id: Long): String {
        if (!mysession.isLoginUser(session)) return "login"
        if (!(session.getAttribute("admin") as Boolean)) throw IllegalAccessException("잘못된 접근입니다.")

        if (id == 1L) {
            return "redirect:/users"
        }

        serviceModule.deleteSelectedUser(id)

        return "redirect:/users"
    }

    @PostMapping("/login")
    fun postLogin(session: HttpSession,
                  @RequestParam(value = "email") email: String,
                  @RequestParam(value = "pass") password: String): String {
        return serviceModule.getLoginResult(session, email, password)
    }

    @GetMapping("/logout")
    fun logout(session: HttpSession): String {
        session.removeAttribute("session_user")
        session.removeAttribute("admin")
        session.removeAttribute("lilactvUser")
        session.removeAttribute("userID")
        return "index"
    }

    @PostMapping("/register")
    fun register(model: Model,
                 @RequestParam(value = "name") name: String,
                 @RequestParam(value = "email") email: String,
                 @RequestParam(value = "mobile") mobile: String,
                 @RequestParam(value = "lilactvID") lilactvID: String,
                 @RequestParam(value = "pass") password: String,
                 @RequestParam(value = "cpass") cpassword: String,
                 response: HttpServletResponse): String {

        val user = Users(name, email, mobile, cpassword)
        return serviceModule.getRegisterResult(user, lilactvID, response)
    }

    @PutMapping("/updateUser")
    fun update(session: HttpSession,
                 @RequestParam(value = "name") name: String,
                 @RequestParam(value = "email") email: String,
                 @RequestParam(value = "mobile") mobile: String,
                 @RequestParam(value = "lilactvID") lilactvID: String,
                 @RequestParam(value = "pass") password: String,
                 @RequestParam(value = "cpass") cpassword: String,
                 response: HttpServletResponse): String {

        if (!(serviceModule.updateUserInfo(Users(name, email, mobile, password), lilactvID, response)))
            return "updateUser"

        return if (session.getAttribute("admin") as Boolean) "redirect:/users" else "index"
    }
}