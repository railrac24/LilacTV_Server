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
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Controller
class QnaController {

    @Autowired
    lateinit var mysession: HttpSessionUtils

    @Autowired
    lateinit var qnaDB: QnaRepo

    @Autowired
    lateinit var util: Utils

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
        qnaDB.save(Questions(loginUser, title, content))
        return "redirect:/qnalist"
    }

    @GetMapping("/qnas/{id}")
    fun showContent(model: Model, @PathVariable id: Long): String {
        model["question"] = qnaDB.getOne(id)
        return "show"
    }

    @GetMapping("/qnas/{id}/form")
    fun editContent(model: Model, @PathVariable id: Long, session: HttpSession, response: HttpServletResponse): String {
        if (!mysession.isLoginUser(session)) return "redirect:/login"
        val loginUser = mysession.getUserFromSession(session) as Users
        val qnaData = qnaDB.getOne(id)
        return if (loginUser.id == qnaData.writer.id) {
            model["question"] = qnaDB.getOne(id)
            "updateMessage"
        } else {
            util.printAlert("<script>alert('You can edit your own post.'); history.go(-1);</script>", response)
            "redirect:/show"
        }
    }

    @PutMapping("/sendmessage/{id}")
    fun updateContent(session: HttpSession,
                      @PathVariable id: Long,
                      @RequestParam(value = "title") title: String,
                      @RequestParam(value = "content") content: String): String {
        if (!mysession.isLoginUser(session)) return "redirect:/login"
        val loginUser = mysession.getUserFromSession(session) as Users
        val qnaData = qnaDB.getOne(id)
        if (loginUser.id == qnaData.writer.id) {
            qnaData.title = title
            qnaData.content = content
            qnaDB.save(qnaData)
        } else throw IllegalAccessException("잘못된 접근입니다.")
        return "redirect:/qnas/$id"
    }

    @DeleteMapping("/qnas/{id}")
    fun deletePost(session: HttpSession, response: HttpServletResponse, @PathVariable id: Long): String {
        if (!mysession.isLoginUser(session)) return "redirect:/login"
        val loginUser = mysession.getUserFromSession(session) as Users
        val qnaData = qnaDB.getOne(id)
        return if (loginUser.id == qnaData.writer.id) {
            qnaDB.deleteById(id)
            "redirect:/qnalist"
        } else {
            util.printAlert("<script>alert('You can delete your own post.'); history.go(-1);</script>", response)
            "redirect:/show"
        }
    }
}