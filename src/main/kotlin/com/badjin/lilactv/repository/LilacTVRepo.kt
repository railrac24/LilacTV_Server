package com.badjin.lilactv.repository

import com.badjin.lilactv.model.*
import org.springframework.data.jpa.repository.JpaRepository


interface ItemRepo: JpaRepository<Items, Long> {
    fun findAllByOnline(online: Boolean): MutableList<Items>?
    fun findByMacaddeth0(macaddeth0: String): Items?
    fun findByOwner(owner: Users): Items?
}

interface UserRepo: JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?
    fun findByResetToken(resetToken: String): Users?
}

interface QnaRepo: JpaRepository<Questions, Long> {
    fun findAllByOrderByIdDesc(): MutableList<Questions>
    fun findAllByWriter(writer: Users): MutableList<Questions>?
}

interface AnswerRepo: JpaRepository<Answers, Long> {
    fun findAllByReplier(replier: Users): MutableList<Answers>?
}

interface StatusRepo: JpaRepository<Status, Long> {

}

interface SubscriptionRepo: JpaRepository<Subscription, Long> {
    fun findByLilacTvId(item: Items): Subscription
    fun deleteByLilacTvId(item: Items)
}