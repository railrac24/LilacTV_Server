package com.badjin.lilactv.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Users (
        @JsonProperty
        var name: String,
        @JsonProperty
        var email: String,
        @JsonProperty
        var mobile: String,
        @JsonIgnore
        var password: String,
//    @OneToMany(mappedBy = "owner", cascade = [CascadeType.ALL])
//    var lilactv: MutableList<Items>?,
        @JsonProperty
        @Id @GeneratedValue var id: Long? = null
){
//    constructor(name: String, email: String, mobile: String, password: String): this(name, email, mobile, password, null)
}