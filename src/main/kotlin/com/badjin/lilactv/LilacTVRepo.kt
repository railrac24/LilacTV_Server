package com.badjin.lilactv

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ItemRepo: JpaRepository<Items, Long> {
    fun findAllByOnline(online: Boolean): MutableList<Items>?
    fun findByMacaddeth0(macaddeth0: String): Items?
}

interface UserRepo: JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?
}