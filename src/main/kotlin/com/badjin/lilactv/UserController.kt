package com.badjin.lilactv

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import java.io.PrintWriter
import java.security.MessageDigest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Controller
class UserController {

    @Autowired
    lateinit var itemDB: ItemRepo

    @Autowired
    lateinit var userDB: UserRepo

    fun crypto(ss: String): String {
        val sha = MessageDigest.getInstance("SHA-256")
        val hexa = sha.digest(ss.toByteArray())

        return hexa.fold("", {
            str, it -> str + "%02x".format(it)
        })
    }

    fun getMacAndID(id: String): Pair<String, Long> {
        var mac: String = ""

        for (i in 0..8 step 2) {
            mac += id.substring(i,i+2) + ':'
        }
        mac += id.substring(10,12)
        val unitID = id.substring(12,14).toLong(radix = 16)

        return Pair(mac, unitID)
    }

    fun getLilacTVID(mac: String, index: Long?): String {
        val macID: String = mac.replace(":","")
        var unitID = ""

        if (index != null) {
            unitID = "%02x".format(index)
        }
        return macID+unitID
    }

    fun printAlert(msg: String, response: HttpServletResponse) {
        val out: PrintWriter = response.writer
        out.println(msg)
        out.flush()
    }

    fun checkingLilacTV(user: Users, lilactvID: String, response: HttpServletResponse): Boolean {
        val (mac_add, deviceID) = getMacAndID(lilactvID)
        val unit: Items? = itemDB.findByMacaddeth0(mac_add)
        val out: PrintWriter

        when {
            unit != null -> if (unit.id == deviceID) {
                if (unit.owner?.id == 1L ) {
                    unit.owner = user
                    itemDB.save(unit)
                } else {
                    printAlert("<script>alert('이미 등록된 제품ID 입니다.'); history.go(-1);</script>", response)
                    return false
                }
            } else {
                printAlert("<script>alert('잘못된 제품ID 입니다.'); history.go(-1);</script>", response)
                return false
            }
            else -> {
                printAlert("<script>alert('잘못된 제품ID 입니다.'); history.go(-1);</script>", response)
                return false
            }
        }

        return true
    }

    @GetMapping("/users")
    fun users(model: Model, session: HttpSession): String {
        session.getAttribute("session_user") ?: return "login"
        if (!(session.getAttribute("admin") as Boolean)) throw IllegalAccessException("잘못된 접근입니다.")

        val owner: MutableList<Users> = userDB.findAll()
        model["owner"] = owner
        return "users"
    }

    @GetMapping("/{id}/form")
    fun updateUserData(model: Model, session: HttpSession, @PathVariable id: Long): String {
        val tempuser = session.getAttribute("session_user") ?: return "login"
        val sessionUser: Users = tempuser as Users
        if (id != sessionUser.id){
            if (!(session.getAttribute("admin") as Boolean)) throw IllegalAccessException("잘못된 접근입니다.")
        }

        if (id == 1L) {
            return "redirect:/users"
        }

        val user = userDB.getOne(id)
        var checked = ""
        var macID = ""
        val unit = itemDB.findByOwner(user)
        if (unit != null) {
            if (unit.owner?.id!! > 1L) {
                checked = "checked"
                macID = getLilacTVID(unit.macaddeth0, unit.id)
            }
        }
        model["lilactv"] = checked
        model["lilactvID"] = macID
        model["user"] = user
        return "updateUser"
    }

    @GetMapping("/{id}/delete")
    fun deleteSelected(session: HttpSession, @PathVariable id: Long): String {
        session.getAttribute("session_user") ?: return "login"
        if (!(session.getAttribute("admin") as Boolean)) throw IllegalAccessException("잘못된 접근입니다.")

        if (id == 1L) {
            return "redirect:/users"
        }

        val user = userDB.getOne(id)
        val unit = itemDB.findByOwner(user)
        if (unit != null) {
            if (unit.owner?.id!! > 1L) {
                unit.owner = userDB.getOne(1L)
                itemDB.save(unit)
            }
        }
        userDB.deleteById(id)

        return "redirect:/users"
    }

    @PostMapping("/login")
    fun postLogin(session: HttpSession,
                  @RequestParam(value = "email") email: String,
                  @RequestParam(value = "pass") password: String): String {
        var pageName = ""
        try {
            val dbUser = userDB.findByEmail(email) ?: return "redirect:/login"
            val cryptoPass = crypto(password)

            if (dbUser.password == cryptoPass) {
                pageName = "index"
                val unit = itemDB.findByOwner(dbUser)
                session.setAttribute("session_user", dbUser)
                session.setAttribute("admin", (dbUser.email == "admin@test.com" || dbUser.email == "railrac23@gmail.com"))
                session.setAttribute("lilactvUser", (unit != null))
                session.setAttribute("userID", dbUser.id)
            } else {
                pageName = "redirect:/login"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return pageName
    }

    @GetMapping("/logout")
    fun logout(session: HttpSession): String {
        session.removeAttribute("session_user")
        session.removeAttribute("admin")
        session.removeAttribute("lilactvUser")
        session.removeAttribute("userID")
        return "index"
    }

    @PostMapping("/register")
    fun register(model: Model,
                 @RequestParam(value = "name") name: String,
                 @RequestParam(value = "email") email: String,
                 @RequestParam(value = "mobile") mobile: String,
                 @RequestParam(value = "lilactvID") lilactvID: String,
                 @RequestParam(value = "pass") password: String,
                 @RequestParam(value = "cpass") cpassword: String,
                 response: HttpServletResponse): String {
        try {
            val cryptoPass = crypto(password)

            if (lilactvID != "") {
                if (! checkingLilacTV(Users(name, email, mobile, cryptoPass), lilactvID, response)) {
                    return "register"
                }
            } else
                userDB.save(Users(name, email, mobile, cryptoPass))

        } catch (e: Exception){
            e.printStackTrace()
            printAlert("<script>alert('이미 등록된 이메일 주소 입니다.'); history.go(-1);</script>", response)
            return "register"
        }

        return "login"
    }

    @PutMapping("/updateUser")
    fun update(session: HttpSession,
                 @RequestParam(value = "name") name: String,
                 @RequestParam(value = "email") email: String,
                 @RequestParam(value = "mobile") mobile: String,
                 @RequestParam(value = "lilactvID") lilactvID: String,
                 @RequestParam(value = "pass") password: String,
                 @RequestParam(value = "cpass") cpassword: String,
                 response: HttpServletResponse): String {

        val cryptoPass = if (cpassword.isNotBlank()) crypto(password) else userDB.findByEmail(email)?.password
        if (cryptoPass != null) {
            try {
                val modUser = Users(name, email, mobile, cryptoPass)
                val out: PrintWriter
                modUser.id = userDB.findByEmail(email)?.id

                if (lilactvID.isNotBlank()) {
                    val (mac_add, deviceID) = getMacAndID(lilactvID)
                    val unit: Items? = itemDB.findByMacaddeth0(mac_add)

                    when {
                        unit != null -> if (unit.id == deviceID) {
                            when {
                                unit.owner?.id == 1L -> {
                                    unit.owner = modUser
                                    itemDB.save(unit)
                                }
                                unit.owner?.id == modUser.id -> userDB.save(modUser)
                                else -> {
                                    printAlert("<script>alert('This ID is already registered.'); history.go(-1);</script>", response)
                                }
                            }
                        } else {
                            printAlert("<script>alert('Incorrect product ID.'); history.go(-1);</script>", response)
                        }
                        else -> printAlert("<script>alert('Incorrect product ID.'); history.go(-1);</script>", response)
                    }
                } else {
                    val unit = userDB.findByEmail(email)?.let { itemDB.findByOwner(it) }
                    if (unit != null) {
                        if (unit.owner?.id!! > 1L) {
                            unit.owner = userDB.getOne(1L)
                            itemDB.save(unit)
                        }
                    }
                    userDB.save(modUser)
                }


            } catch (e: Exception) {
                e.printStackTrace()
                printAlert("<script>alert('This email is already registered.'); history.go(-1);</script>", response)
                return "updateUser"
            }
        }

        return if (session.getAttribute("admin") as Boolean) "redirect:/users" else "index"
    }
}