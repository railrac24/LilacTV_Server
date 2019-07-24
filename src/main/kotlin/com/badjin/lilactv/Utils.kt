package com.badjin.lilactv

import java.security.MessageDigest

class Utils {

    fun crypto(ss: String): String {
        val sha = MessageDigest.getInstance("SHA-256")
        val hexa = sha.digest(ss.toByteArray())

        return hexa.fold("", {
            str, it -> str + "%02x".format(it)
        })
    }

    fun setIndex(units: MutableList<Items>): MutableList<Items> {
        for (i  in units.indices) {
            units[i].seqindex = i+1
        }
        return units
    }

    fun checkLilacTVID(id: String): Pair<String, Long> {
        val mac = "1234"
        val unitID: Long = 12
        return Pair(mac, unitID)
    }
}

