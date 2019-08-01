package com.badjin.lilactv.services

import com.badjin.lilactv.model.Users
import org.springframework.stereotype.Service
import javax.servlet.http.HttpSession

@Service
class HttpSessionUtils {
    private val USER_SESSION_KEY = "session_user"

    fun isLoginUser(session: HttpSession): Boolean {
        val sessionUser = session.getAttribute(USER_SESSION_KEY)
        return sessionUser != null
    }

    fun getUserFromSession(session: HttpSession): Users? {
        if (!isLoginUser(session)) return null
        return session.getAttribute(USER_SESSION_KEY) as Users
    }
}