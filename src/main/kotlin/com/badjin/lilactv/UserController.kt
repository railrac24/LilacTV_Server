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

    fun checkingLilacTV(user: Users, lilactvID: String, response: HttpServletResponse): Boolean {
        val (mac_add, deviceID) = getMacAndID(lilactvID)
        val unit: Items? = itemDB.findByMacaddeth0(mac_add)
        val out: PrintWriter

        if (unit != null) {
            if (unit.id == deviceID) {
                if (unit.owner?.id == 1L ) {
                    unit.owner = user
                    itemDB.save(unit)
                } else {
                    out = response.writer
                    out.println("<script>alert('이미 등록된 제품ID 입니다.'); history.go(-1);</script>")
                    out.flush()
                    return false
                }
            } else {
                out = response.writer
                out.println("<script>alert('잘못된 제품ID 입니다.'); history.go(-1);</script>")
                out.flush()
                return false
            }
        } else {
            out = response.writer
            out.println("<script>alert('잘못된 제품ID 입니다.'); history.go(-1);</script>")
            out.flush()
            return false
        }

        return true
    }

    @GetMapping("/users")
    fun users(model: Model): String {
        val owner: MutableList<Users> = userDB.findAll()
        model["owner"] = owner
        return "users"
    }

    @GetMapping("/{id}/form")
    fun updateUserData(model: Model, @PathVariable id: Long): String {

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
    fun deleteSelected(@PathVariable id: Long): String {

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
    fun postLogin(model: Model,
                  session: HttpSession,
                  @RequestParam(value = "email") email: String,
                  @RequestParam(value = "pass") password: String): String {
        var pageName = ""
        try {
            val cryptoPass = crypto(password)
            val dbUser = userDB.findByEmail(email)

            if (dbUser != null) {
                pageName = if (dbUser.password == cryptoPass) {
                    session.setAttribute("email", dbUser.email)
                    model["name"] = dbUser.name
                    "main"
                } else {
                    "login"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pageName
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
            val out: PrintWriter = response.writer
            out.println("<script>alert('이미 등록된 이메일 주소 입니다.'); history.go(-1);</script>")
            out.flush()
            return "register"
        }

        return "login"
    }

    @PutMapping("/updateUser")
    fun update(  @RequestParam(value = "name") name: String,
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

                    if (unit != null) {
                        if (unit.id == deviceID) {
                            when {
                                unit.owner?.id == 1L -> {
                                    unit.owner = modUser
                                    itemDB.save(unit)
                                }
                                unit.owner?.id == modUser.id -> userDB.save(modUser)
                                else -> {
                                    out = response.writer
                                    out.println("<script>alert('This ID is already registered.'); history.go(-1);</script>")
                                    out.flush()
                                }
                            }
                        } else {
                            out = response.writer
                            out.println("<script>alert('Incorrect product ID.'); history.go(-1);</script>")
                            out.flush()
                        }
                    } else {
                        out = response.writer
                        out.println("<script>alert('Incorrect product ID.'); history.go(-1);</script>")
                        out.flush()
                    }
                } else userDB.save(modUser)

            } catch (e: Exception) {
                e.printStackTrace()
                val out: PrintWriter = response.writer
                out.println("<script>alert('This email is already registered.'); history.go(-1);</script>")
                out.flush()
                return "updateUser"
            }
        }
        return "redirect:/users"
    }
}