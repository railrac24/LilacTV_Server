package com.badjin.lilactv

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import java.security.MessageDigest
import javax.servlet.http.HttpSession

@Controller
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

        return hexa.fold("", {
            str, it -> str + "%02x".format(it)
        })
    }

    fun setIndex(units: MutableList<Devices>): MutableList<Devices> {
        for (i  in units.indices) {
            units[i].index = i+1
        }
        return units
    }

    @GetMapping("/{pageTag}")
    fun htmlPage(model: Model, @PathVariable pageTag: String): String {
        if (pageTag == "devicelist") {
            return "redirect:/devicelist"
        }
        return pageTag
    }

    @GetMapping("/devicelist")
    fun devicelist(model: Model): String {
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
        return "devicelist"
    }

    @PostMapping("/update")
    fun update(model: Model, @RequestParam(name = "ListMode") sortMode: String): String {
        listAllFlag = sortMode == "all"
        return "redirect:/devicelist"
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
                pageName = if (dbUser.password == cryptoPass) {
                    session.setAttribute("email", dbUser.email)
                    model["first_name"] = dbUser.first_name
                    model["last_name"] = dbUser.last_name
                    "welcome"
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
                 @RequestParam(value = "first_name") first_name: String,
                 @RequestParam(value = "last_name") last_name: String,
                 @RequestParam(value = "email") email: String,
                 @RequestParam(value = "pass") password: String,
                 @RequestParam(value = "cpass") cpassword: String): String {
        try {
            val cryptoPass = crypto(password)
            userrepo.save(Users(first_name, last_name, email, cryptoPass))
        } catch (e: Exception){
            e.printStackTrace()
        }

        return "login"
    }

    @GetMapping("users")
    fun users(model: Model): String {
        val owner: MutableList<Users>? = userrepo.findAll()
        val names: MutableList<String>? = null

        if (owner != null) {
            for (i  in owner.indices) names?.set(i, owner[i].first_name+" "+owner[i].last_name)
        }

        return "users"
    }

}


