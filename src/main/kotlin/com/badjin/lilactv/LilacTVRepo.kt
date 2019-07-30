package com.badjin.lilactv

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.security.acl.Owner



interface ItemRepo: JpaRepository<Items, Long> {
    fun findAllByOnline(online: Boolean): MutableList<Items>?
    fun findByMacaddeth0(macaddeth0: String): Items?
    fun findByOwner(owner: Users): Items?
}

interface UserRepo: JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?
}