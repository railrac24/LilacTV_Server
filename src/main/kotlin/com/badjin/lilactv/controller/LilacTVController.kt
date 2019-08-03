package com.badjin.lilactv.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*

@Controller
class LilacTVController {

//    @GetMapping("/{pageTag}")
//    fun htmlPage(model: Model, @PathVariable pageTag: String): String {
////        if (pageTag == "items") {
////            return "redirect:/items"
////        } else if (pageTag == "users") {
////            return "redirect:/users"
////        }
//        return pageTag
//    }

    @GetMapping("/index")
    fun mainPage(): String {
//        for (i in 1..5) model["path$i"] = false
//        model["path1"] = true
        return "index"
    }

    @GetMapping("/manual")
    fun manualPage(): String {
        return "manual"
    }

    @GetMapping("/qnaList")
    fun qnaPage(): String {
        return "qnaList"
    }

    @GetMapping("/firmware")
    fun firmwarePage(): String {
        return "firmware"
    }
}


