package com.badjin.lilactv.services

import com.badjin.lilactv.model.Items
import org.springframework.stereotype.Component
import java.security.MessageDigest

@Component
class Utils {

    fun setIndex(units: MutableList<Items>?): MutableList<Items>? {
        if (units != null) {
            for (i  in units.indices) {
                units[i].seqindex = i+1
            }
        }
        return units
    }

    fun crypto(ss: String): String {
        val sha = MessageDigest.getInstance("SHA-256")
        val hexa = sha.digest(ss.toByteArray())

        return hexa.fold("", {
            str, it -> str + "%02x".format(it)
        })
    }

}