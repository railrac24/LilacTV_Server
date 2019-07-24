package com.badjin.lilactv

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@Controller
class LilacTVController {

    private var listAllFlag: Boolean = false

    @Autowired
    lateinit var itemDB: ItemRepo

    @Autowired
    lateinit var userDB: UserRepo

    val utils = Utils()

    @GetMapping("/{pageTag}")
    fun htmlPage(model: Model, @PathVariable pageTag: String): String {
        if (pageTag == "items") {
            return "redirect:/items"
        } else if (pageTag == "users") {
            return "redirect:/users"
        }
        return pageTag
    }

    @GetMapping("/items")
    fun items(model: Model): String {
        var units: MutableList<Items>?

        if (listAllFlag) {
            units = itemDB.findAll()
        }
        else {
            units = itemDB.findAllByOnline(true)
            if (units == null) {
                units = itemDB.findAll()
            }
        }

        model["units"] = utils.setIndex(units)
        return "items"
    }

    @PostMapping("/update")
    fun update(model: Model, @RequestParam(name = "ListMode") sortMode: String): String {
        listAllFlag = sortMode == "all"
        return "redirect:/items"
    }

    @PostMapping("/login")
    fun postLogin(model: Model,
                  session: HttpSession,
                  @RequestParam(value = "email") email: String,
                  @RequestParam(value = "pass") password: String): String {
        var pageName = ""
        try {
            val cryptoPass = utils.crypto(password)
            val dbUser = userDB.findByEmail(email)

            if (dbUser != null) {
                pageName = if (dbUser.password == cryptoPass) {
                    session.setAttribute("email", dbUser.email)
                    model["name"] = dbUser.name
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
                 @RequestParam(value = "name") name: String,
                 @RequestParam(value = "email") email: String,
                 @RequestParam(value = "mobile") mobile: String,
                 @RequestParam(value = "lilactvID") lilactvID: String,
                 @RequestParam(value = "pass") password: String,
                 @RequestParam(value = "cpass") cpassword: String): String {
        try {
            val cryptoPass = utils.crypto(password)
            if (lilactvID != null) {
                val (mac_add, deviceID) = utils.checkLilacTVID(lilactvID)
                val unit: Items? = itemDB.findByMacaddeth0(mac_add)

                if (unit != null) {
                   if (unit.id == deviceID) {
                       unit.owner = Users(name, email, mobile, cryptoPass)
                       println("mac = $mac_add   deviceID = $deviceID")
//                       itemDB.save(unit)
                   } else {
//                       model["errorMsg"] = "잘못된 제품ID 입니다."
                       return "register"
                   }
                } else {
//                     model["errorMsg"] = "잘못된 제품ID 입니다."
                      return "register"
                }
            }
            userDB.save(Users(name, email, mobile, cryptoPass))
        } catch (e: Exception){
            e.printStackTrace()
        }

        return "login"
    }

    @GetMapping("/users")
    fun users(model: Model): String {
        val owner: MutableList<Users>? = userDB.findAll()
        if (owner != null) {
            model["owner"] = owner
        }
        return "users"
    }

}


