package com.badjin.lilactv.controller

import com.badjin.lilactv.services.LilacTVServices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpSession

@Controller
class DeviceController {

    private var listAllFlag: Boolean = true

    @Autowired
    lateinit var serviceModule: LilacTVServices


    @GetMapping("/items")
    fun items(model: Model, session: HttpSession): String {
        session.getAttribute("session_user") ?: return "login"
        if (!(session.getAttribute("admin") as Boolean)) throw IllegalAccessException("잘못된 접근입니다.")

        model["units"] = serviceModule.getDevicesList(listAllFlag)!!
        return "items"
    }


    @PostMapping("/update")
    fun update(model: Model, @RequestParam(name = "ListMode") sortMode: String): String {
        listAllFlag = sortMode == "all"
        return "redirect:/items"
    }

}