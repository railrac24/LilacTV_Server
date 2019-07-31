package com.badjin.lilactv

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
    lateinit var itemDB: ItemRepo

    fun setIndex(units: MutableList<Items>?): MutableList<Items>? {
        if (units != null) {
            for (i  in units.indices) {
                units[i].seqindex = i+1
            }
        }
        return units
    }

    @GetMapping("/items")
    fun items(model: Model, session: HttpSession): String {
        session.getAttribute("session_user") ?: return "login"
        if (!(session.getAttribute("admin") as Boolean)) throw IllegalAccessException("잘못된 접근입니다.")

        var units: MutableList<Items>?
        if (listAllFlag) {
            units = itemDB.findAll()
        }
        else {
            units = itemDB.findAllByOnline(true)
            if (units == null) {
                units = itemDB.findAll()
            }
        }

        model["units"] = setIndex(units)!!
        return "items"
    }

    @PostMapping("/update")
    fun update(model: Model, @RequestParam(name = "ListMode") sortMode: String): String {
        listAllFlag = sortMode == "all"
        return "redirect:/items"
    }

}