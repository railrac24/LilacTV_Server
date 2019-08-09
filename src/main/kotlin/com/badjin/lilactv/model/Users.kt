package com.badjin.lilactv.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Users (
        var name: String,
        var email: String,
        var mobile: String,
        @JsonIgnore
        var password: String,
        var resetToken: String?,
        @Id @GeneratedValue var id: Long? = null
){
    constructor(name: String, email: String, mobile: String, password: String): this(name, email, mobile, password, null)
}