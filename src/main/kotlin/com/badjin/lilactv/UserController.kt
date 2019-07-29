package com.badjin.lilactv

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
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

    fun checkLilacTVID(id: String): Pair<String, Long> {
        var mac: String = ""

        for (i in 0..8 step 2) {
            mac += id.substring(i,i+2) + ':'
        }
        mac += id.substring(10,12)
        val unitID = id.substring(12,14).toLong(radix = 16)

        return Pair(mac, unitID)
    }

    fun getLilacTVID(mac: String, index: Long?): String {
        var macID: String = mac.replace(":","")
        var unitID = ""

        if (index != null) {
            unitID = "%02x".format(index)
        }
        return macID+unitID
    }

    fun CheckingLilacTV(user: Users, lilactvID: String, response: HttpServletResponse): Boolean {
        val (mac_add, deviceID) = checkLilacTVID(lilactvID)
        val unit: Items? = itemDB.findByMacaddeth0(mac_add)
        val customer = user
        val out: PrintWriter

        if (unit != null) {
            if (unit.id == deviceID) {
                if (unit.owner?.id == 1L ) {
                    customer.lilactv?.add(unit)
                    user.lilactv = customer.lilactv
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
        val owner: MutableIterable<Users> = userDB.findAll()
        model["owner"] = owner
        return "users"
    }

    @GetMapping("/{email}/form")
    fun updateUserData(model: Model, @PathVariable email: String): String {
        val user = userDB.findByEmail(email)
        if (user != null) {
            var checked = ""
            var macID = ""
            if (user.lilactv?.isNotEmpty()!!) {
                checked = "checked"
                macID = getLilacTVID(user.lilactv!!.first().macaddeth0, user.lilactv!!.first().id)
            }
            model["lilactv"] = checked
            model["lilactvID"] = macID
            model["user"] = user
        }
        return "updateUser"
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
                if (! CheckingLilacTV(Users(name, email, mobile, cryptoPass, null), lilactvID, response)) {
                    return "register"
                }
            } else
                userDB.save(Users(name, email, mobile, cryptoPass, null))

        } catch (e: Exception){
            e.printStackTrace()
            val out: PrintWriter = response.writer
            out.println("<script>alert('이미 등록된 이메일 주소 입니다.'); history.go(-1);</script>")
            out.flush()
            return "register"
        }

        return "login"
    }

    @PostMapping("/updateUser")
    fun update(model: Model,
                 @RequestParam(value = "name") name: String,
                 @RequestParam(value = "email") email: String,
                 @RequestParam(value = "mobile") mobile: String,
                 @RequestParam(value = "lilactvID") lilactvID: String,
                 @RequestParam(value = "pass") password: String,
                 @RequestParam(value = "cpass") cpassword: String,
                 response: HttpServletResponse): String {
        try {

            if (lilactvID != "") {
                if (! CheckingLilacTV(Users(name, email, mobile, password, null), lilactvID, response)) {
                    return "updateUser"
                }
            } else
                userDB.save(Users(name, email, mobile, password, null))

        } catch (e: Exception){
            e.printStackTrace()
            val out: PrintWriter = response.writer
            out.println("<script>alert('이미 등록된 이메일 주소 입니다.'); history.go(-1);</script>")
            out.flush()
            return "updateUser"
        }

        return "users"
    }
}