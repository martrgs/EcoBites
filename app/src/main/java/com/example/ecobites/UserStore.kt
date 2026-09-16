package com.example.ecobites

import android.content.Context
import com.example.ecobites.model.Product
import org.json.JSONArray
import org.json.JSONObject

/** Datos locales segmentados por correo para que nunca se mezclen entre sesiones. */
class UserStore(context: Context, email: String) {
    private val accountId = email.trim().lowercase().ifBlank { "anonymous" }
    private val prefs = context.getSharedPreferences("ecobites_user_$accountId", Context.MODE_PRIVATE)

    var darkMode: Boolean
        get() = prefs.getBoolean("dark_mode", false)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()
    var notifications: Boolean
        get() = prefs.getBoolean("notifications", true)
        set(value) = prefs.edit().putBoolean("notifications", value).apply()
    var language: String
        get() = prefs.getString("language", "Español") ?: "Español"
        set(value) = prefs.edit().putString("language", value).apply()

    fun products(): MutableList<Product> {
        val raw = prefs.getString("products", "[]") ?: "[]"
        return try {
            val items = JSONArray(raw)
            MutableList(items.length()) { index ->
                val item = items.getJSONObject(index)
                Product(item.getLong("id"), item.getString("name"), item.getString("category"), item.getLong("expiration"), item.optString("quantity"))
            }
        } catch (_: Exception) { mutableListOf() }
    }

    fun saveProducts(products: List<Product>) {
        val json = JSONArray()
        products.forEach { product -> json.put(JSONObject().apply {
            put("id", product.id); put("name", product.name); put("category", product.category)
            put("expiration", product.expirationMillis); put("quantity", product.quantity)
        }) }
        prefs.edit().putString("products", json.toString()).apply()
    }
}
