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
import javax.persistence.criteria.CriteriaBuilder
import javax.servlet.http.HttpSession
import kotlin.collections.ArrayList

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

    var cuPage: Int = 0

    @GetMapping("/qnaList/{page}/{size}")
    fun showList(model: Model,
                 @PathVariable page: Optional<Int>,
                 @PathVariable size: Optional<Int>): String {

        val currentPage = page.orElse(1)
        val pageSize = size.orElse(serviceModule.pageListSize)
        val qna = serviceModule.findPaginated(PageRequest(currentPage - 1, pageSize))

        model["qna"] = qna
        cuPage = currentPage

        val totalPages = qna.totalPages
        if (totalPages > 1) {
            val myPage = serviceModule.getMyPage(qna.totalPages, currentPage - 1)
            model["pageNumbers"] = myPage

            if ((currentPage-1)/serviceModule.pageTagSize == 0) model["firstPage"] = true

            if (currentPage == 1) model["previousPage"] = true
            else model["previousPageNo"] = (currentPage-1).toString()

            if (currentPage == totalPages) model["nextPage"] = true
            else model["nextPageNo"] = (currentPage+1).toString()

            if ((currentPage-1)/serviceModule.pageTagSize == (totalPages-1)/serviceModule.pageTagSize) model["lastPage"] = true
            else model["lastPageNo"] = totalPages.toString()
        } else {
            model["firstPage"] = true
            model["previousPage"] = true
            model["nextPage"] = true
            model["lastPage"] = true
        }

        return "qnaList"
    }

    @GetMapping("/{id}")
    fun showContent(model: Model, @PathVariable id: Long): String {
        val question = qnaDB.getOne(id)
        model["question"] = question
        model["countOfAnswers"] = question.countOfAnswers
        model["currentPage"] = cuPage
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
        return "redirect:/qna/qnaList/1/${serviceModule.pageListSize}"
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
        return "redirect:/qna/qnaList/$cuPage/${serviceModule.pageListSize}"
    }
}