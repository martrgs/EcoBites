package com.example.ecobites.model

data class Product(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val category: String,
    val expirationMillis: Long,
    val quantity: String = ""
) {
    fun daysUntilExpiry(now: Long = System.currentTimeMillis()): Long =
        ((expirationMillis - now) / 86_400_000L).coerceAtLeast(0)
}
