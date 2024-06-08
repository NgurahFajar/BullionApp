package com.example.bullionapp.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Utility {

    fun stringToSha256(input: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(input.toByteArray())
            val hexString = StringBuilder()
            for (byte in hashBytes) {
                val hex = Integer.toHexString(0xff and byte.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

}
