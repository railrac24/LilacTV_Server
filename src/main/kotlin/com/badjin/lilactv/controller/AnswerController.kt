package com.badjin.lilactv.controller

import com.badjin.lilactv.model.Answers
import com.badjin.lilactv.model.Result
import com.badjin.lilactv.model.Users
import com.badjin.lilactv.services.HttpSessionUtils
import com.badjin.lilactv.services.LilacTVServices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.lang.IllegalStateException
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/qna/{questionId}/answers")
class AnswerController {

    @Autowired
    lateinit var loginSession: HttpSessionUtils

    @Autowired
    lateinit var serviceModule: LilacTVServices

    @PostMapping("")
    fun create(@PathVariable questionId: Long,
               @RequestParam(value = "content") content: String,
               session: HttpSession): Answers? {
        return try {
            if (content == "") null
            val loginUser = loginSession.getUserFromSession(session) as Users
            val question = serviceModule.findQnaById(questionId)

            question!!.addAnswer()
            serviceModule.saveAnswer(Answers(loginUser, question, content))

        } catch (e: IllegalStateException) {
            null
        }
    }

    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable questionId: Long,
                   @PathVariable id: Long,
                   session: HttpSession): Result {

        val result = Result(true, 0,null)
        return try {
            val answerData = serviceModule.findAnswerById(id)
            loginSession.hasPermission(session, answerData!!.replier.id)
            val question = serviceModule.findQnaById(questionId)

            question!!.deleteAnswer()
            serviceModule.deleteAnswerById(id)

            result.count = question.countOfAnswers
            result

        } catch (e: IllegalStateException) {
            result.resultFail(e.message!!)
        }
    }

    @GetMapping("/{id}")
    fun deleteGet(@PathVariable questionId: Long,
                  @PathVariable id: Long): String {
        return "questionShow/$questionId"
    }
}