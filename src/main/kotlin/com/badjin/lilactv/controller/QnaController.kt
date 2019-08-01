package com.badjin.lilactv.controller

import com.badjin.lilactv.model.Questions
import com.badjin.lilactv.model.Users
import com.badjin.lilactv.repository.QnaRepo
import com.badjin.lilactv.services.HttpSessionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.servlet.http.HttpSession

@Controller
class QnaController {

    @Autowired
    lateinit var mysession: HttpSessionUtils

    @Autowired
    lateinit var qnaDB: QnaRepo

    @GetMapping("/qnalist")
    fun showList(model: Model): String {
        val qnas = qnaDB.findAll()
        model["qnas"] = qnas
        return "qnalist"
    }

    @GetMapping("/qnas/form")
    fun sendMessage(session: HttpSession): String {
        if (!mysession.isLoginUser(session)) return "redirect:/login"
        return "sendmessage"
    }

    @PutMapping("sendmessage")
    fun saveContent(session: HttpSession,
                 @RequestParam(value = "title") title: String,
                 @RequestParam(value = "content") content: String): String {
        if (!mysession.isLoginUser(session)) return "redirect:/login"

        val loginUser = mysession.getUserFromSession(session) as Users

//        println("name = ${loginUser.name}  title = $title  content = $content")
        qnaDB.save(Questions(loginUser, title, content))
        return "redirect:/qnalist"
    }

    @GetMapping("/qnas/{id}")
    fun showContent(model: Model, @PathVariable id: Long): String {
        model["question"] = qnaDB.getOne(id)
        return "show"
    }
}