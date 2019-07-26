package com.badjin.lilactv

import javax.persistence.*

@Entity
data class Users (
    var name: String,
    var email: String,
    var mobile: String,
    var password: String,
    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL])
    var lilactv: MutableList<Items>?,
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
    @ManyToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "owner_id")
    var owner: Users?
)