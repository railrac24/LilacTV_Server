package com.badjin.lilactv.repository

import com.badjin.lilactv.model.Items
import com.badjin.lilactv.model.Questions
import com.badjin.lilactv.model.Users
import org.springframework.data.jpa.repository.JpaRepository


interface ItemRepo: JpaRepository<Items, Long> {
    fun findAllByOnline(online: Boolean): MutableList<Items>?
    fun findByMacaddeth0(macaddeth0: String): Items?
    fun findByOwner(owner: Users): Items?
}

interface UserRepo: JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?
}

interface QnaRepo: JpaRepository<Questions, Long> {

}