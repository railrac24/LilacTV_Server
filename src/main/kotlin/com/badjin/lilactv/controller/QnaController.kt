package com.badjin.lilactv.controller

import com.badjin.lilactv.model.Questions
import com.badjin.lilactv.model.Users
import com.badjin.lilactv.repository.QnaRepo
import com.badjin.lilactv.services.HttpSessionUtils
import com.badjin.lilactv.services.LilacTVServices
import com.badjin.lilactv.services.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import java.lang.IllegalStateException
import java.util.*
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/qna")
class QnaController {

    @Autowired
    lateinit var loginSession: HttpSessionUtils

    @Autowired
    lateinit var qnaDB: QnaRepo

    @Autowired
    lateinit var util: Utils

    @Autowired
    lateinit var serviceModule: LilacTVServices

    @GetMapping("/qnaList/{page}/{size}")
    fun showList(model: Model,
                 @PathVariable page: Optional<Int>,
                 @PathVariable size: Optional<Int>): String {

        val currentPage = page.orElse(1)
        val pageSize = size.orElse(5)
        val qna = serviceModule.findPaginated(PageRequest.of(currentPage - 1, pageSize))
        val actives = Array(qna.totalPages) {false}
        actives[currentPage-1] = true
        model["qna"] = qna

        val totalPages = qna.totalPages
        if (totalPages > 0) {
            val pageNumbers = Array(totalPages) { i -> (i+1).toString() }
            val pageMap = mutableMapOf<String, Boolean>()
            for (i in 1..totalPages) {
                pageMap[i.toString()] = actives[i]
                print("value = ${pageMap[i.toString()]}")
            }
//            for (i in pageNumbers) print("i = $i,")

            model["pageNumbers"] = pageNumbers
//            model["active"] = actives

            if (currentPage == 1) model["firstPage"] = true
            if (currentPage == totalPages) model["lastPage"] = true

//            for (i in pageNumbers) print("i = $i,")
            println("total = ${qna.totalPages}, pages = $pageMap, current = ${qna.number}")
        }

        return "qnaList"
    }

    @GetMapping("/{id}")
    fun showContent(model: Model, @PathVariable id: Long): String {
        var question = qnaDB.getOne(id)
        model["question"] = question
        model["countOfAnswers"] = question.countOfAnswers
        return "questionShow"
    }

    @GetMapping("/form")
    fun sendMessage(model: Model, session: HttpSession): String {
        try {
            loginSession.isLoginUser(session)
        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "questionForm"
    }

    @PutMapping("/questionSubmit")
    fun saveContent(model: Model, session: HttpSession,
                 @RequestParam(value = "title") title: String,
                 @RequestParam(value = "content") content: String): String {
        try {
            val loginUser = loginSession.getUserFromSession(session) as Users

            qnaDB.save(Questions(loginUser, title, content))

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "redirect:/qna/qnaList"
    }

    @GetMapping("/{id}/form")
    fun editContent(model: Model, @PathVariable id: Long, session: HttpSession): String {
        try {
            val qnaData = qnaDB.getOne(id)
            loginSession.hasPermission(session, qnaData.writer)

            model["question"] = qnaDB.getOne(id)

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "questionUpdate"
    }

    @PutMapping("/questionSubmit/{id}")
    fun updateContent(model: Model, session: HttpSession,
                      @PathVariable id: Long,
                      @RequestParam(value = "title") title: String,
                      @RequestParam(value = "content") content: String): String {

        try {
            val qnaData = qnaDB.getOne(id)
            loginSession.hasPermission(session, qnaData.writer)

            qnaData.updateContent(title, content)
            qnaDB.save(qnaData)

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "redirect:/qna/$id"
    }

    @DeleteMapping("/{id}")
    fun deletePost(model: Model, session: HttpSession, @PathVariable id: Long): String {

        try {
            val qnaData = qnaDB.getOne(id)
            loginSession.hasPermission(session, qnaData.writer)

            qnaDB.deleteById(id)

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "redirect:/qna/qnaList"
    }
}