package com.badjin.lilactv.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Users (
        var name: String,
        var email: String,
        var mobile: String,
        var password: String,
//    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL])
//    var lilactv: MutableList<Items>?,
        @Id @GeneratedValue var id: Long? = null
){
//    constructor(name: String, email: String, mobile: String, password: String): this(name, email, mobile, password, null)
}