package com.badjin.lilactv

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Devices (
        var mac_add_eth0: String,
        var mac_add_wlan: String,
        var ip_add: String,
        var registered: String,
        var active: Boolean,
        var index: Int?,
        @Id @GeneratedValue var id : Long? = null
)