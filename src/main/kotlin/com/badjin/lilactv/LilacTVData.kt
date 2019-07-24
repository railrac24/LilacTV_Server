package com.badjin.lilactv

import javax.persistence.*
import javax.persistence.FetchType



@Entity
data class Users (
    var name: String,
    var email: String,
    var mobile: String,
    var password: String,
    @Id @GeneratedValue var id: Long? = null
)

@Entity
data class Items (
    var macaddeth0: String,
    var macaddwlan: String,
    var ipadd: String,
    var online: Boolean,
    var tvheadend: Boolean,
    var seqindex: Int?,
    @Id @GeneratedValue var id : Long? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "owner_id")
    var owner: Users?
)
