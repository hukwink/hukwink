package com.hukwink.hukwink.adapter.larksuite.util

import com.hukwink.hukwink.util.sha256
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object LarksuiteWebhookDecrypt {
    fun decrypt(encryptKeySha256: ByteArray, content: String): ByteArray {
        val rawContent = Base64.getDecoder().decode(content)

        val cipher = Cipher.getInstance("AES/CBC/NOPADDING")
        val iv = rawContent.copyOf(16)
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(encryptKeySha256, "AES"), IvParameterSpec(iv))

        val result = cipher.doFinal(rawContent, 16, rawContent.size - 16)
        if (result.isEmpty()) {
            return result
        }

        var strLen = result.size
        while (strLen > 1) {
            val crtByte = result[strLen - 1].toInt().and(0xFF)
            if (crtByte > 16) {
                return if (strLen == result.size) {
                    result
                } else {
                    result.copyOf(strLen)
                }
            } else {
                strLen--
            }
        }
        return ByteArray(0)
    }

    @JvmStatic
    fun main(a: Array<String>) {
        val result = decrypt(
            "test key".byteInputStream().sha256(),
            "P37w+VZImNgPEO1RBhJ6RtKl7n6zymIbEG1pReEzghk="
        ).decodeToString()
        if (result != "hello world") {
            error("Failed")
        }
    }
}