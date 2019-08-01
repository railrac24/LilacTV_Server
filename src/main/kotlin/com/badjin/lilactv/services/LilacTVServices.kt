package com.badjin.lilactv.services

import com.badjin.lilactv.repository.ItemRepo
import com.badjin.lilactv.model.Items
import com.badjin.lilactv.repository.UserRepo
import com.badjin.lilactv.model.Users
import org.apache.commons.lang3.SystemUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Service
class LilacTVServices {

    @Autowired
    lateinit var itemDB: ItemRepo

    @Autowired
    lateinit var userDB: UserRepo

    @Autowired
    lateinit var util: Utils

    fun getMacAndID(id: String): Pair<String, Long> {
        var mac: String = ""

        for (i in 0..8 step 2) {
            mac += id.substring(i,i+2) + ':'
        }
        mac += id.substring(10,12)
        val unitID = id.substring(12,14).toLong(radix = 16)

        return Pair(mac, unitID)
    }

    fun getProductTVID(mac: String, index: Long?): String {
        val macID: String = mac.replace(":","")
        var unitID = ""

        if (index != null) {
            unitID = "%02x".format(index)
        }
        return macID+unitID
    }

    fun updateItemInfo(user: Users, lilactvID: String, response: HttpServletResponse): Boolean {
        val (mac_add, deviceID) = getMacAndID(lilactvID)
        val unit: Items? = itemDB.findByMacaddeth0(mac_add)

        when {
            unit != null -> if (unit.id == deviceID) {
                if (unit.owner?.id == 1L ) {
                    unit.owner = user
                    itemDB.save(unit)
                } else {
                    util.printAlert("<script>alert('이미 등록된 제품ID 입니다.'); history.go(-1);</script>", response)
                    return false
                }
            } else {
                util.printAlert("<script>alert('잘못된 제품ID 입니다.'); history.go(-1);</script>", response)
                return false
            }
            else -> {
                util.printAlert("<script>alert('잘못된 제품ID 입니다.'); history.go(-1);</script>", response)
                return false
            }
        }

        return true
    }

    fun getDevicesList(sortMode: Boolean): MutableList<Items>? {
        var units: MutableList<Items>?
        val system =  if (SystemUtils.IS_OS_WINDOWS) "-n" else "-c"

        if (sortMode) {
            units = itemDB.findAll()
        }
        else {
            units = itemDB.findAllByOnline(true)
            if (units == null) units = itemDB.findAll()
//            else {
//                for (i  in units.indices) {
//                    if (!(util.pingTest(units[i].ipadd, system))){
//                        units[i].online = false
//                        itemDB.save(units[i])
//                    }
//                }
//                units = itemDB.findAllByOnline(true)
//                if (units == null) units = itemDB.findAll()
//            }
        }

        return util.setIndex(units)
    }

    fun getUserList(): MutableList<Users>? {
        return userDB.findAll()
    }

    fun getSelectedUser4Edit(id: Long): Triple<Users, String, String> {
        var checked = ""
        var macID = ""
        val user = userDB.getOne(id)
        val unit = itemDB.findByOwner(user)
        if (unit != null) {
            if (unit.owner?.id!! > 1L) {
                checked = "checked"
                macID = getProductTVID(unit.macaddeth0, unit.id)
            }
        }
        return Triple(user, checked, macID)
    }

    fun deleteSelectedUser(id: Long) {
        val unit = itemDB.findByOwner(userDB.getOne(id))
        if (unit != null) {
            if (unit.owner?.id!! > 1L) {
                unit.owner = userDB.getOne(1L)
                itemDB.save(unit)
            }
        }
        userDB.deleteById(id)
    }

    fun getLoginResult(session: HttpSession, email: String, password: String): String {
        var pageName = ""
        try {
            val dbUser = userDB.findByEmail(email) ?: return "redirect:/login"
            val cryptoPass = util.crypto(password)

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

    fun getRegisterResult(user: Users, lilactvID: String, response: HttpServletResponse): String {
        try {
            val cryptoPass = util.crypto(user.password)

            if (lilactvID != "") {
                if (! updateItemInfo(Users(user.name, user.email, user.mobile, cryptoPass), lilactvID, response)) {
                    return "register"
                }
            } else
                userDB.save(Users(user.name, user.email, user.mobile, cryptoPass))

        } catch (e: Exception){
            e.printStackTrace()
            util.printAlert("<script>alert('이미 등록된 이메일 주소 입니다.'); history.go(-1);</script>", response)
            return "register"
        }

        return "login"
    }

    fun updateUserInfo(user: Users, lilactvID: String, response: HttpServletResponse): Boolean {
        val cryptoPass = if (user.password.isNotBlank()) util.crypto(user.password) else userDB.findByEmail(user.email)?.password
        if (cryptoPass != null) {
            try {
                val modUser = Users(user.name, user.email, user.mobile, cryptoPass)
                modUser.id = userDB.findByEmail(user.email)?.id

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
                                    util.printAlert("<script>alert('This ID is already registered.'); history.go(-1);</script>", response)
                                }
                            }
                        } else {
                            util.printAlert("<script>alert('Incorrect product ID.'); history.go(-1);</script>", response)
                        }
                        else -> util.printAlert("<script>alert('Incorrect product ID.'); history.go(-1);</script>", response)
                    }
                } else {
                    val unit = userDB.findByEmail(user.email)?.let { itemDB.findByOwner(it) }
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
                util.printAlert("<script>alert('This email is already registered.'); history.go(-1);</script>", response)
                return false
            }
        }
        return true
    }
}