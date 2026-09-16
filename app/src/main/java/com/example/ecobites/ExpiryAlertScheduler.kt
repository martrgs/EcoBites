package com.example.ecobites

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.ecobites.model.Product

object ExpiryAlertScheduler {
    private const val DAY_MILLIS = 24 * 60 * 60 * 1000L
    private val ALERT_DAYS = intArrayOf(5, 4, 3, 2, 0)

    fun schedule(context: Context, product: Product, email: String) {
        if (!UserStore(context, email).notifications) return
        val now = System.currentTimeMillis()
        if (product.expirationMillis <= now) return
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        cancelProduct(context, product, email)

        val upcoming = ALERT_DAYS.filter { day -> product.expirationMillis - day * DAY_MILLIS > now }
        upcoming.forEach { day -> setAlarm(context, product, email, day, product.expirationMillis - day * DAY_MILLIS) }

        // Si se registró un producto dentro de la ventana de cinco días, avisa una sola vez
        // de acuerdo con los días que le quedan y conserva los avisos futuros.
        val remainingDays = kotlin.math.ceil((product.expirationMillis - now).toDouble() / DAY_MILLIS).toInt()
        if (remainingDays in 1..5 && remainingDays !in upcoming) {
            setAlarm(context, product, email, remainingDays, now + 3_000L)
        }
    }

    fun cancelAll(context: Context, products: List<Product>, email: String) {
        products.forEach { cancelProduct(context, it, email) }
    }

    private fun cancelProduct(context: Context, product: Product, email: String) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        (ALERT_DAYS.toList() + (1..5).toList()).distinct().forEach { day ->
            PendingIntent.getBroadcast(
                context,
                requestCode(email, product.id, day),
                notificationIntent(context, product, email, day),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )?.let { pendingIntent -> alarmManager.cancel(pendingIntent); pendingIntent.cancel() }
        }
    }

    private fun setAlarm(context: Context, product: Product, email: String, days: Int, triggerAt: Long) {
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode(email, product.id, days), notificationIntent(context, product, email, days),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        context.getSystemService(AlarmManager::class.java)
            .setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
    }

    private fun requestCode(email: String, productId: Long, days: Int) = "$email:$productId:$days".hashCode()

    private fun notificationIntent(context: Context, product: Product, email: String, days: Int) =
        Intent(context, ExpiryNotificationReceiver::class.java).apply {
            putExtra(ExpiryNotificationReceiver.EXTRA_ACCOUNT_EMAIL, email)
            putExtra(ExpiryNotificationReceiver.EXTRA_PRODUCT_ID, product.id)
            putExtra(ExpiryNotificationReceiver.EXTRA_PRODUCT_NAME, product.name)
            putExtra(ExpiryNotificationReceiver.EXTRA_DAYS_REMAINING, days)
        }
}
