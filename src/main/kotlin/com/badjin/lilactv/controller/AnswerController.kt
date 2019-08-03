package com.badjin.lilactv.controller

import com.badjin.lilactv.model.Answers
import com.badjin.lilactv.model.Users
import com.badjin.lilactv.repository.AnswerRepo
import com.badjin.lilactv.repository.QnaRepo
import com.badjin.lilactv.services.HttpSessionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import java.lang.IllegalStateException
import javax.servlet.http.HttpSession

@Controller
@RequestMapping("/qna/{questionId}/answers")
class AnswerController {

    @Autowired
    lateinit var loginSession: HttpSessionUtils

    @Autowired
    lateinit var qnaDB: QnaRepo

    @Autowired
    lateinit var answerDB: AnswerRepo

    @PostMapping("")
    fun create(model: Model, session: HttpSession,
               @PathVariable questionId: Long,
               @RequestParam(value = "content") content: String): String {
        try {
            val loginUser = loginSession.getUserFromSession(session) as Users
            val question = qnaDB.getOne(questionId)

            answerDB.save(Answers(loginUser, question, content))

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "redirect:/qna/$questionId"
    }

    @DeleteMapping("/{id}")
    fun deletePost(model: Model, session: HttpSession,
                   @PathVariable questionId: Long,
                   @PathVariable id: Long): String {

        try {
            val answerData = answerDB.getOne(id)
            loginSession.hasPermission(session, answerData.replier)

            answerDB.deleteById(id)

        } catch (e: IllegalStateException) {
            model["errorMsg"] = e.message!!
            return "login"
        }
        return "redirect:/qna/$questionId"
    }
}