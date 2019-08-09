package com.badjin.lilactv.model

data class Result (
    var valid: Boolean,
    var count: Int,
    var errorMessage: String?
){
    fun resultFail(errorMessage: String): Result {
        return Result(false, 0, errorMessage)
    }
}