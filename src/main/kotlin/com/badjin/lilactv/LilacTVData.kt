package com.badjin.lilactv

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Items (
        var macaddeth0: String,
        var macaddwlan: String,
        var ipadd: String,
        var online: Boolean,
        var tvheadend: Boolean,
        var seqindex: Int?,
        @Id @GeneratedValue var id : Long? = null
)

@Entity
data class Users (
        var name: String,
        var email: String,
        var mobile: String,
        var deviceid: Long? = null,
        var password: String,
        @Id @GeneratedValue var id: Long? = null
)

