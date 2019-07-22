package com.badjin.lilactv

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import java.security.MessageDigest
import javax.servlet.http.HttpSession

@Controller
//@RequestMapping("device")
class LilacTVController {

    private var listAllFlag: Boolean = false

    @Autowired
    lateinit var unitrepo: deviceRepo

    @Autowired
    lateinit var userrepo: userRepo

//    @GetMapping("/index")
//    fun index(): String {
//        return "index"
//    }
//
//    @GetMapping("/welcome")
//    fun welcome(): String {
//        return "welcome"
//    }
//
//    @GetMapping("/login")
//    fun login(): String {
//        return "login"
//    }
//
//    @GetMapping("/register")
//    fun register(): String {
//        return "register"
//    }

    fun crypto(ss: String): String {
        val sha = MessageDigest.getInstance("SHA-256")
        val hexa = sha.digest(ss.toByteArray())
        val cryptoStr = hexa.fold("", {
            str, it -> str + "%02x".format(it)
        })

        return cryptoStr
    }

    fun setIndex(units: MutableList<Devices>): MutableList<Devices> {
        for (i  in units.indices) {
            units[i].index = i+1
        }
        return units
    }

    @GetMapping("/{pageTag}")
    fun htmlPage(model: Model, @PathVariable pageTag: String): String {
        if (pageTag == "list") {
            return "redirect:/list"
        }
        return pageTag
    }

    @GetMapping("/list")
    fun list(model: Model): String {
        var units: MutableList<Devices>?

        if (listAllFlag) {
            units = unitrepo.findAll()
        }
        else {
            units = unitrepo.findAllByActive(true)
            if (units == null) {
                units = unitrepo.findAll()
            }
        }
        model["units"] = setIndex(units)
        return "list"
    }

    @PostMapping("/update")
    fun update(model: Model, @RequestParam(name = "ListMode") sortMode: String): String {
        listAllFlag = sortMode == "all"
        return "redirect:/list"
    }

//    @PostMapping("/create")
//    fun create(): String {
//        return "redirect:/list"
//    }

    @PostMapping("/login")
    fun postLogin(model: Model,
                  session: HttpSession,
                  @RequestParam(value = "email") email: String,
                  @RequestParam(value = "pass") password: String): String {
        var pageName = ""
        try {
            val cryptoPass = crypto(password)
            val dbUser = userrepo.findByEmail(email)

            if (dbUser != null) {
                if (dbUser.password == cryptoPass) {
                    session.setAttribute("email", dbUser.email)
                    model["firstname"] = dbUser.firstName
                    model["lastname"] = dbUser.lastName
                    pageName = "welcome"
                }
                else {
                    pageName = "login"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return pageName
    }

    @PostMapping("/register")
    fun postSign(model: Model,
                 @RequestParam(value = "fname") firstname: String,
                 @RequestParam(value = "lname") lastname: String,
                 @RequestParam(value = "email") email: String,
                 @RequestParam(value = "pass") password: String,
                 @RequestParam(value = "cpass") cpassword: String): String {
        try {
            val cryptoPass = crypto(password)
            userrepo.save(Users(firstname, lastname, email, cryptoPass))
        } catch (e: Exception){
            e.printStackTrace()
        }

        return "login"
    }

}


