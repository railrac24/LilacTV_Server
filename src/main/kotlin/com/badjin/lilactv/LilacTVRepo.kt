package com.badjin.lilactv

import org.springframework.data.jpa.repository.JpaRepository

interface ItemRepo: JpaRepository<Items, Long> {
    fun findAllByOnline(online: Boolean): MutableList<Items>?
    fun findByMacaddeth0(macaddeth0: String): Items?
}

interface UserRepo: JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?
}