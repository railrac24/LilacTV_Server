package com.badjin.lilactv

import org.springframework.data.jpa.repository.JpaRepository

interface deviceRepo: JpaRepository<Devices, Long> {
    fun findAllByActive(active: Boolean): MutableList<Devices>?
}

interface userRepo: JpaRepository<Users, Long> {
    fun findByEmail(email: String): Users?
}