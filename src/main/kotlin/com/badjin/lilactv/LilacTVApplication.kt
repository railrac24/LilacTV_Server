package com.badjin.lilactv

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class LilacTVApplication

fun main(args: Array<String>) {
    runApplication<LilacTVApplication>(*args)
}
