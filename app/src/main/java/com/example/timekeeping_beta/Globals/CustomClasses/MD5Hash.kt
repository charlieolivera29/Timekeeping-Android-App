package com.example.timekeeping_beta.Globals.CustomClasses

import java.security.MessageDigest
import kotlin.experimental.and

class MD5Hash {

    fun compare(user_pin: String, entered_password: String): Boolean {

        val md = MessageDigest.getInstance("MD5")
        md.update(entered_password.trim().toByteArray(Charsets.UTF_8))
        val digest = md.digest()
        val hexString = StringBuffer(digest.size * 2)

        for (i in digest) {

            val b = i and 0xFF.toByte()

            if (b < 0x10) {
                hexString.append('0')
            }
            hexString.append(Integer.toHexString(b.toInt()))
        }

        val hs = hexString.toString().replace("0ffffff", "")

        return hs == user_pin
    }
}