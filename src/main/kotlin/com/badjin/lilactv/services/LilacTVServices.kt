package com.badjin.lilactv.services

import com.badjin.lilactv.*
import com.badjin.lilactv.model.*
import com.badjin.lilactv.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import javax.servlet.http.HttpSession
import kotlin.math.min

@Service
class LilacTVServices {

    @Autowired
    lateinit var itemDB: ItemRepo

    @Autowired
    lateinit var userDB: UserRepo

    @Autowired
    lateinit var qnaDB: QnaRepo

    @Autowired
    lateinit var answerDB: AnswerRepo

    @Autowired
    lateinit var subscriptionDB: SubscriptionRepo

    @Autowired
    lateinit var statusDB: StatusRepo

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

//    fun updateItemInfo(user: Users, lilactvID: String): Int {
//        val (mac_add, deviceID) = getMacAndID(lilactvID)
//        val unit: Items? = itemDB.findByMacaddeth0(mac_add)
//
//        when {
//            unit != null -> if (unit.id == deviceID) {
//                if (unit.owner?.id == 1L ) {
//                    unit.owner = user
//                    itemDB.save(unit)
//                } else return 1
//            } else return 2
//            else -> return 1
//        }
//        return 0
//    }

    fun getDevicesList(sortMode: Int): MutableList<Items>? {
        var units: MutableList<Items>?

        when (sortMode) {
            ALL_LIST -> units = itemDB.findAll()
            ONLINE_LIST -> {
                units = itemDB.findAllByOnline(true)
                if (units == null) units = itemDB.findAll()
            }
            else -> units = itemDB.findAll()
        }
        return util.setIndex(units)
    }

    fun getAllSubscriptions(): MutableList<Subscription> {
        return subscriptionDB.findAll()
    }

    fun getUserList(): MutableList<Users>? {
        return userDB.findAll()
    }

    fun findUserByEmail(email: String): Users? {
        return userDB.findByEmail(email)
    }

    fun findUserById(id: Long): Users? {
        return userDB.getOne(id)
    }

    fun isRegisteredEmail(email: String): Users {
        return (userDB.findByEmail(email) ?: throw IllegalStateException("등록되지 않은 Email 주소 입니다."))
    }

    fun findUserByResetToken(resetToken: String): Users? {
        return userDB.findByResetToken(resetToken)
    }

    fun isRegisteredToken(resetToken: String): Users {
        return (userDB.findByResetToken(resetToken) ?: throw IllegalStateException("잘못된 비밀번호 재설정 링크 입니다."))
    }

    fun findItemByOwner(user: Users): Items? {
        return (itemDB.findByOwner(user))
    }

    fun saveUser(user: Users) {
        userDB.save(user)
    }

    fun findQnaById(id: Long): Questions? {
        return qnaDB.getOne(id)
    }

    fun saveQna(question: Questions) {
        qnaDB.save(question)
    }

    fun deleteQnaById(id: Long) {
        qnaDB.deleteById(id)
    }

    fun findAnswerById(id: Long): Answers? {
        return answerDB.getOne(id)
    }

    fun saveAnswer(answers: Answers): Answers {
        return answerDB.save(answers)
    }

    fun deleteAnswerById(id: Long) {
        answerDB.deleteById(id)
    }

    fun getSelectedUser4Edit(id: Long): Triple<Users, String, String> {
        var checked = ""
        var macID = ""
        val user = userDB.getOne(id)
        val unit = itemDB.findByOwner(user)
        if (unit != null) {
            if (unit.owner?.id!! > ADMIN) {
                checked = "checked"
                macID = getProductTVID(unit.macaddeth0, unit.id)
            }
        }
        return Triple(user, checked, macID)
    }

    fun getSelectedUser4Edit(email: String): Triple<Users?, String, String> {
        var checked = ""
        var macID = ""
        val user = userDB.findByEmail(email)
        val unit = user?.let { itemDB.findByOwner(it) }
        if (unit != null) {
            if (unit.owner?.id!! > ADMIN) {
                checked = "checked"
                macID = getProductTVID(unit.macaddeth0, unit.id)
            }
        }
        return Triple(user, checked, macID)
    }

    fun checkStatus(sub: Subscription) {
        if (sub.status.state == "Activated") {
            if (LocalDateTime.now().isAfter(sub.endDate)) {
                sub.status = statusDB.getOne(EXPIRED)
                subscriptionDB.save(sub)
            }
        }
    }

    fun getSubscription(id: Long): Subscription {
        val sub: Subscription
        val lilactvID = itemDB.findByOwner(findUserById(id)!!)
        if (lilactvID != null) {
            sub = subscriptionDB.findByLilacTvId(lilactvID)
            checkStatus(sub)
        } else {
            sub = Subscription(
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    statusDB.getOne(WAIT)
            )
        }
        return sub
    }

    fun setSubscription(sub: Subscription, id: Long, state: Long) {
        sub.lilacTvId = itemDB.findByOwner(findUserById(id)!!)
        sub.status = statusDB.getOne(state)
        subscriptionDB.save(sub)
    }

    fun deleteSelectedUser(id: Long) {
        val user = userDB.getOne(id)
        val unit = itemDB.findByOwner(user)
        val questions = qnaDB.findAllByWriter(user)
        val answers = answerDB.findAllByReplier(user)
        if (unit != null) {
            if (unit.owner?.id!! > ADMIN) {
                unit.owner = userDB.getOne(ADMIN)
                itemDB.save(unit)
                subscriptionDB.delete(subscriptionDB.findByLilacTvId(unit))
            }
        }
        if (questions != null) qnaDB.deleteAll(questions)
        if (answers != null) answerDB.deleteAll(answers)
        userDB.deleteById(id)
    }

    fun loginProcess(session: HttpSession, email: String, password: String) {

        val dbUser = userDB.findByEmail(email) ?: throw IllegalStateException("등록되지 않은 사용자 입니다.")
        val cryptoPass = util.crypto(password)

        if (dbUser.password == cryptoPass) {
            val unit = itemDB.findByOwner(dbUser)
            session.setAttribute("session_user", dbUser)
            session.setAttribute("admin", (dbUser.email == "admin@test.com" || dbUser.email == "railrac23@gmail.com"))
            session.setAttribute("lilactvUser", (unit != null))
        } else throw IllegalStateException("비밀번호가 일치하지 않습니다.")
    }

    fun registerProcess(user: Users) {

        if (userDB.findByEmail(user.email) != null) throw IllegalStateException("이미 등록된 이메일 주소 입니다.")

        val cryptoPass = util.crypto(user.password)

        userDB.save(Users(user.name, user.email, user.mobile, cryptoPass))
    }

    fun updateUserInfo(user: Users, lilactvID: String?): Boolean {
        val cryptoPass = if (user.password.isNotBlank()) util.crypto(user.password) else userDB.findByEmail(user.email)?.password
        if (cryptoPass != null) {
            val modUser = Users(user.name, user.email, user.mobile, cryptoPass)
            modUser.id = userDB.findByEmail(user.email)?.id

            if (lilactvID != null) {
                if (lilactvID.isNotBlank()) {
                    val (mac_add, deviceID) = getMacAndID(lilactvID)
                    val unit: Items? = itemDB.findByMacaddeth0(mac_add)

                    when {
                        unit != null -> if (unit.id == deviceID) {
                            when {
                                unit.owner?.id == ADMIN -> {
                                    unit.owner = modUser
                                    itemDB.save(unit)
                                }
                                unit.owner?.id == modUser.id -> userDB.save(modUser)
                                else -> throw IllegalStateException("이미 등록된 제품ID 입니다.")
                            }
                        } else throw IllegalStateException("잘못된 제품ID 입니다.")
                        else -> throw IllegalStateException("잘못된 제품ID 입니다.")
                    }
                } else {
                    val unit = userDB.findByEmail(user.email)?.let { itemDB.findByOwner(it) }
                    if (unit != null) {
                        if (unit.owner?.id!! > ADMIN) {
                            unit.owner = userDB.getOne(ADMIN)
                            itemDB.save(unit)
                            subscriptionDB.delete(subscriptionDB.findByLilacTvId(unit))
                        }
                    }
                    userDB.save(modUser)
                }
            } else userDB.save(modUser)
        }
        return true
    }

    data class MyPage(val active: Boolean, val page: String)

    fun getMyPage(size: Int, currentPage: Int): ArrayList<MyPage> {
        val myPage = arrayListOf<MyPage>()
        val pageTag = currentPage / TOTAL_PAGE_SIZE
        val startPageTag = pageTag * TOTAL_PAGE_SIZE
        val endPageTag = if ((startPageTag+(TOTAL_PAGE_SIZE-1)) <= (size-1)) startPageTag+2 else size-1

        for (i in startPageTag .. endPageTag)
            myPage.add(MyPage((currentPage == i), (i+1).toString()))
        return myPage
    }

    fun findPaginated(pageable: Pageable): Page<Questions> {
        val pageSize = pageable.pageSize

        val currentPage = if (qnaDB.count() > 0) {
            val tPage = if ((qnaDB.count() % TOTAL_LIST_SIZE) > 0) 1 else 0
            val totalPage = qnaDB.count() / TOTAL_LIST_SIZE + tPage
            if (pageable.pageNumber <= totalPage - 1) pageable.pageNumber else (totalPage - 1).toInt()
        } else 0

        val startItem = currentPage * pageSize
        val list: MutableList<Questions>
        val questions = qnaDB.findAllByOrderByIdDesc()

        list = if (questions.size < startItem) {
            Collections.emptyList()
        } else {
            val toIndex = min(startItem + pageSize, questions.size)
            questions.subList(startItem, toIndex)
        }

        return PageImpl(list, PageRequest(currentPage, pageSize), questions.size.toLong())
    }
}