package com.badjin.lilactv

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LilacTVApplication

const val ADMIN = 1L

const val ALL_LIST = 100
const val ONLINE_LIST = 101
const val OWNER_LIST = 102

const val WAIT = 1L
const val ACTIVATED = 2L
const val EXPIRED = 3L
const val ACTIVATE_PERIOD = 1L

const val TOTAL_PAGE_SIZE = 10
const val TOTAL_LIST_SIZE = 8

const val USER_SESSION_KEY = "session_user"

fun main(args: Array<String>) {
    runApplication<LilacTVApplication>(*args)
}
