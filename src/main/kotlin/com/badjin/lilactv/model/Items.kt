package com.badjin.lilactv.model

import javax.persistence.*

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