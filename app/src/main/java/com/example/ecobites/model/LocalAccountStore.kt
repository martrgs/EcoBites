package com.example.ecobites.model

import android.content.Context
import java.security.MessageDigest

data class LocalAccount(val name: String, val email: String)

/** Registro local para las cuentas creadas con correo dentro de la aplicación. */
class LocalAccountStore(context: Context) {
    private val prefs = context.getSharedPreferences("ecobites_accounts", Context.MODE_PRIVATE)

    fun register(name: String, email: String, password: String): Boolean {
        val normalizedEmail = email.trim().lowercase()
        if (prefs.contains(passwordKey(normalizedEmail))) return false
        prefs.edit()
            .putString(nameKey(normalizedEmail), name.trim())
            .putString(passwordKey(normalizedEmail), password.sha256())
            .apply()
        return true
    }

    fun authenticate(email: String, password: String): LocalAccount? {
        val normalizedEmail = email.trim().lowercase()
        val savedHash = prefs.getString(passwordKey(normalizedEmail), null) ?: return null
        if (savedHash != password.sha256()) return null
        val name = prefs.getString(nameKey(normalizedEmail), normalizedEmail.substringBefore('@'))
            ?: normalizedEmail.substringBefore('@')
        return LocalAccount(name, normalizedEmail)
    }

    private fun passwordKey(email: String) = "password_${email.sha256()}"
    private fun nameKey(email: String) = "name_${email.sha256()}"

    private fun String.sha256(): String = MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .joinToString("") { "%02x".format(it) }
}
