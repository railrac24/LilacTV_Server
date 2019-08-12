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
@RequestMapping("/admin")
class AdminController {

    private var listAllFlag: Boolean = true

    @Autowired
    lateinit var serviceModule: LilacTVServices

    @Autowired
    lateinit var loginSession: HttpSessionUtils

    @GetMapping("/userList")
    fun users(model: Model, session: HttpSession): String {
        try {
            if (loginSession.hasPermission(session)) {
                model["owner"] = serviceModule.getUserList()!!
            }
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "userList"
    }

    @GetMapping("/{id}/delete")
    fun deleteSelected(session: HttpSession, model: Model, @PathVariable id: Long): String {
        try {
            if (loginSession.hasPermission(session)) {
                serviceModule.deleteSelectedUser(id)
            }
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        if (id == 1L) {
            return "redirect:/admin/userList"
        }
        return "redirect:/admin/userList"
    }

    @GetMapping("/itemList")
    fun items(model: Model, session: HttpSession): String {
        try {
            if (loginSession.hasPermission(session)) {
                model["units"] = serviceModule.getDevicesList(listAllFlag)!!
            }
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "itemList"
    }

    @PostMapping("/update")
    fun update(model: Model,
               session: HttpSession,
               @RequestParam(name = "ListMode") sortMode: String): String {
        try {
            if (loginSession.hasPermission(session)) {
                listAllFlag = sortMode == "all"
            }
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "redirect:/admin/itemList"
    }

    @GetMapping("/{id}/form")
    fun editUserInfo(model: Model,
                     session: HttpSession,
                     @PathVariable id: Long): String {

        try {
            if (loginSession.hasPermission(session)) {
                val (user, checked, productID) = serviceModule.getSelectedUser4Edit(id)
                val subscription = serviceModule.getSubscription(id)
                model["subscription"] = subscription
                model["lilactv"] = checked == "checked"
                model["lilactvID"] = productID
                model["user"] = user
            }

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }

        return "adminUser"
    }

}