package com.badjin.lilactv.controller

import com.badjin.lilactv.model.Questions
import com.badjin.lilactv.model.Users
import com.badjin.lilactv.repository.QnaRepo
import com.badjin.lilactv.services.HttpSessionUtils
import com.badjin.lilactv.services.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import java.lang.IllegalStateException
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/qna")
class QnaController {

    @Autowired
    lateinit var mysession: HttpSessionUtils

    @Autowired
    lateinit var qnaDB: QnaRepo

    @Autowired
    lateinit var util: Utils

    @GetMapping("/qnaList")
    fun showList(model: Model): String {
        val qna = qnaDB.findAll()
        model["qna"] = qna
        return "/qna/qnaList"
    }

    @GetMapping("/{id}")
    fun showContent(model: Model, @PathVariable id: Long): String {
        model["question"] = qnaDB.getOne(id)
        return "/qna/questionShow"
    }

    @GetMapping("/form")
    fun sendMessage(model: Model, session: HttpSession): String {
        try {
            mysession.isLoginUser(session)
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "/users/login"
        }
        return "/qna/questionForm"
    }

    @PutMapping("/questionSubmit")
    fun saveContent(model: Model, session: HttpSession,
                 @RequestParam(value = "title") title: String,
                 @RequestParam(value = "content") content: String): String {
        try {
            val loginUser = mysession.getUserFromSession(session) as Users

            qnaDB.save(Questions(loginUser, title, content))

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "/users/login"
        }
        return "redirect:/qna/qnaList"
    }

    @GetMapping("/{id}/form")
    fun editContent(model: Model, @PathVariable id: Long, session: HttpSession): String {
        try {
            val qnaData = qnaDB.getOne(id)
            mysession.hasPermission(session, qnaData.writer)

            model["question"] = qnaDB.getOne(id)

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "/users/login2"
        }
        return "/qna/questionUpdate"
    }

    @PutMapping("/questionSubmit/{id}")
    fun updateContent(model: Model, session: HttpSession,
                      @PathVariable id: Long,
                      @RequestParam(value = "title") title: String,
                      @RequestParam(value = "content") content: String): String {

        try {
            val qnaData = qnaDB.getOne(id)
            mysession.hasPermission(session, qnaData.writer)

            qnaData.updateContent(title, content)
            qnaDB.save(qnaData)

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "/users/login2"
        }
        return "redirect:/qna/$id"
    }

    @DeleteMapping("/{id}")
    fun deletePost(model: Model, session: HttpSession, @PathVariable id: Long): String {

        try {
            val qnaData = qnaDB.getOne(id)
            mysession.hasPermission(session, qnaData.writer)

            qnaDB.deleteById(id)

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "/users/login2"
        }
        return "redirect:/qna/qnaList"
    }
}