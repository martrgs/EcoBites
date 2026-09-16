package com.example.ecobites.model

import android.content.Context

data class UserSession(
    val name: String,
    val email: String,
    val provider: String
)

/** Mantiene la cuenta activa y permite que cada UserStore use siempre el mismo perfil. */
class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("ecobites_session", Context.MODE_PRIVATE)

    fun save(session: UserSession) {
        prefs.edit()
            .putString(KEY_NAME, session.name)
            .putString(KEY_EMAIL, session.email.lowercase())
            .putString(KEY_PROVIDER, session.provider)
            .apply()
    }

    fun current(): UserSession? {
        val email = prefs.getString(KEY_EMAIL, null)?.trim().orEmpty()
        if (email.isBlank()) return null
        return UserSession(
            name = prefs.getString(KEY_NAME, email.substringBefore('@')) ?: email.substringBefore('@'),
            email = email,
            provider = prefs.getString(KEY_PROVIDER, "email") ?: "email"
        )
    }

    fun clear() = prefs.edit().clear().apply()

    private companion object {
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_PROVIDER = "provider"
    }
}
