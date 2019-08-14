package com.badjin.lilactv.controller

import com.badjin.lilactv.*
import com.badjin.lilactv.model.Users
import com.badjin.lilactv.services.HttpSessionUtils
import com.badjin.lilactv.services.LilacTVServices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import java.lang.IllegalStateException
import java.time.LocalDateTime
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/admin")
class AdminController {

    private var listAllFlag = ALL_LIST

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
        if (id == ADMIN) {
            return "redirect:/admin/userList"
        }
        return "redirect:/admin/userList"
    }

    @GetMapping("/itemList")
    fun items(model: Model, session: HttpSession): String {
        try {
            if (loginSession.hasPermission(session)) {
                model["all"] = true
                if (listAllFlag == OWNER_LIST) {
                    model["all"] = false
                    model["subscriptions"] = serviceModule.getAllSubscriptions()
                }
                else
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
                listAllFlag = sortMode.toInt()
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

    @PostMapping("/updateUser")
    fun updateUser(session: HttpSession, model: Model,
                              @RequestParam(value = "name") name: String,
                              @RequestParam(value = "email") email: String,
                              @RequestParam(value = "mobile") mobile: String,
                              @RequestParam(value = "pass") password: String): String {

        val id = serviceModule.findUserByEmail(email)?.id
        try {
            if (loginSession.hasPermission(session)) {
                serviceModule.updateUserInfo(Users(name, email, mobile, password), null)
            }
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            val (user, checked, macID) = serviceModule.getSelectedUser4Edit(email)
            if (user != null) {
                model["user"] = user
            }
            return "adminUser"
        }
        return "redirect:/admin/$id/form"

    }

    @PostMapping("/updateUserWithLilacTV")
    fun updateUserWithLilacTV(session: HttpSession, model: Model,
                   @RequestParam(value = "name") name: String,
                   @RequestParam(value = "email") email: String,
                   @RequestParam(value = "mobile") mobile: String,
                   @RequestParam(value = "productID") productID: String,
                   @RequestParam(value = "status_value") status_value: String,
                   @RequestParam(value = "pass") password: String): String {

        val id = serviceModule.findUserByEmail(email)?.id
        try {
            if (loginSession.hasPermission(session)) {
                var itemID = productID
                if (productID != "") {
                    val subscription = serviceModule.getSubscription(id!!)
                    if (status_value != subscription.status.state) {
                        when (status_value) {
                            "Activated" -> {
                                subscription.endDate = LocalDateTime.now().plusDays(ACTIVATE_PERIOD)
                                serviceModule.setSubscription(subscription,id, ACTIVATED)
                            }
                            "Expired" -> {
                                subscription.endDate = LocalDateTime.now().minusDays(1)
                                serviceModule.setSubscription(subscription,id, EXPIRED)
                            }
                            "Wait" -> itemID = ""
                        }
                    }
                }
                serviceModule.updateUserInfo(Users(name, email, mobile, password), itemID)
            }

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            val (euser, checked, macID) = serviceModule.getSelectedUser4Edit(email)
            model["lilactv"] = checked == "checked"
            model["lilactvID"] = macID
            if (euser != null) {
                model["user"] = euser
            }
            return "adminUser"
        }
        return "redirect:/admin/$id/form"
    }
}