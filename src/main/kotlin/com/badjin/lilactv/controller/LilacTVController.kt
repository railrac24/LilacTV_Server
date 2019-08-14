package com.badjin.lilactv.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class LilacTVController {

    @GetMapping("/index")
    fun mainPage(): String {
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
}


