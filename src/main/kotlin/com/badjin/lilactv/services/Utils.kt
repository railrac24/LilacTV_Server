package com.badjin.lilactv.services

import com.badjin.lilactv.model.Items
import org.springframework.stereotype.Component
import java.io.PrintWriter
import java.security.MessageDigest
import javax.servlet.http.HttpServletResponse
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.IOException
import java.util.ArrayList



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

    fun printAlert(msg: String, response: HttpServletResponse) {
        val out: PrintWriter = response.writer
        out.println(msg)
        out.flush()
    }


    @Throws(IOException::class)
    fun doCommand(command: List<String>, count: String): Boolean {
        var s: String?
        var result: String? = null

        val pb = ProcessBuilder(command)
        val process = pb.start()

        val stdInput = BufferedReader(InputStreamReader(process.inputStream))

        // read the output from the command
        s = stdInput.readLine()
        while (s != null) {
//            println(s)
            s = stdInput.readLine()
            if (s != null) {
                if (s.startsWith(count)) {
                    result = s.substringBeforeLast(", ").substringAfterLast(", ")
                }
            }
        }

        if (result == "100% packet loss") return false
        return true
    }

    fun pingTest(userIP: String, systemTag: String): Boolean {
        val commands = ArrayList<String>()
        val count: String = "1"
        commands.add("ping")
        commands.add(systemTag)
        commands.add(count)
        commands.add(userIP)
        return doCommand(commands, count)
    }

}