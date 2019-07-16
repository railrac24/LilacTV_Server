package com.badjin.lilactv

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class LilacController {

    private var listAllFlag: Boolean = false

    @Autowired
    lateinit var repository: DeviceRepo

    @GetMapping("/index")
    fun index(): String {
        return "index"
    }

    @GetMapping("/form")
    fun form(): String {
        return "form"
    }

//    @GetMapping("/{pageTag}")
//    fun htmlPage(model: Model, @PathVariable pageTag: String, @RequestParam(name = "ListMode") sortMode: String): String {
//        if (pageTag == "list") {
//            if (sortMode == "all") {
//                println("aaaaa")
//                val allUnit = repository.findAll()
//                model["units"] = allUnit
//            }
//            else {
//                println("bbbbb")
//                val units = repository.findAllByActive(true)
//                if (units != null) { //check out
//                    for (i  in units.indices) {
//                        units[i].index = i+1
//                    }
//                    model["units"] = units
//                }
//            }
//            return "redirect:/list"
//        }
//        return pageTag
//    }

    @GetMapping("/list")
    fun list(model: Model): String {
        var units: MutableList<Devices>?

        if (listAllFlag) {
            units = repository.findAll()
            for (i  in units.indices) {
                units[i].index = i+1
            }
            model["units"] = units
        }
        else {
            units = repository.findAllByActive(true)
            if (units != null) { //check out
                for (i  in units.indices) {
                    units[i].index = i+1
                }
                model["units"] = units
            } else {
                units = repository.findAll()
                for (i  in units.indices) {
                    units[i].index = i+1
                }
                model["units"] = units
            }
        }

        return "list"
    }

    @PostMapping("/update")
    fun update(model: Model, @RequestParam(name = "ListMode") sortMode: String): String {
        listAllFlag = sortMode == "all"
        return "redirect:/list"
    }

    @PostMapping("/create")
    fun create(): String {
        return "redirect:/list"
    }

}


