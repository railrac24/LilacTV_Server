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

    fun setIndex(units: MutableList<Items>?): MutableList<Items>? {
        if (units != null) {
            for (i  in units.indices) {
                units[i].seqindex = i+1
            }
        }
        return units
    }

    fun checkLilacTVID(id: String): Pair<String, Long> {
        var mac: String = ""

        for (i in 0..8 step 2) {
            mac += id.substring(i,i+2) + ':'
        }
        mac += id.substring(10,12)
        val unitID = id.substring(12,14).toLong(radix = 16)

        return Pair(mac, unitID)
    }
}

