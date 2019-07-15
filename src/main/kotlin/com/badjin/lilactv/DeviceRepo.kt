package com.badjin.lilactv

import org.springframework.data.jpa.repository.JpaRepository

interface DeviceRepo: JpaRepository<Devices, Long> {
    fun findAllByActive(active: Boolean): MutableList<Devices>?
}