package com.designlife.setwork.common.utils.security

import android.util.Base64
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator


object EncryptionUtils {

    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    private const val ENCRYPTION_ALGORITHM = "AES"

    fun generateKey(): Key {
        val keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM)
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }

    suspend fun encrypt(text: String, key: Key): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    suspend fun decrypt(text: String, key: Key): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decryptedBytes = cipher.doFinal(Base64.decode(text, Base64.DEFAULT))
        return String(decryptedBytes)
    }
}