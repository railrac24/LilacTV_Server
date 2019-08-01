package com.badjin.lilactv.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class LilacTVController {

    @GetMapping("/{pageTag}")
    fun htmlPage(model: Model, @PathVariable pageTag: String): String {
        if (pageTag == "items") {
            return "redirect:/items"
        } else if (pageTag == "users") {
            return "redirect:/users"
        }
        return pageTag
    }
}


