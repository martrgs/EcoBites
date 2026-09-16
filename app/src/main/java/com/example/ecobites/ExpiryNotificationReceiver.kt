package com.example.ecobites

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ExpiryNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val productId = intent.getLongExtra(EXTRA_PRODUCT_ID, -1)
        if (productId < 0L) return
        val email = intent.getStringExtra(EXTRA_ACCOUNT_EMAIL).orEmpty()
        if (email.isNotBlank() && !UserStore(context, email).notifications) return
        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Vencimientos", NotificationManager.IMPORTANCE_HIGH))
        }
        val openApp = PendingIntent.getActivity(context, productId.hashCode(), Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val name = intent.getStringExtra(EXTRA_PRODUCT_NAME).orEmpty()
        val days = intent.getIntExtra(EXTRA_DAYS_REMAINING, 5)
        val title = if (days == 0) "Producto vencido" else "Producto próximo a vencer"
        val message = if (days == 0) {
            "${name} ya venció. Revísalo antes de consumirlo."
        } else {
            "${name} vence en ${days} ${if (days == 1) "día" else "días"}. Revísalo para evitar desperdicios."
        }
        manager.notify("$email:$productId".hashCode(), NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true).setContentIntent(openApp).build())
    }
    companion object {
        const val CHANNEL_ID = "ecobites_expiry"
        const val EXTRA_PRODUCT_ID = "product_id"
        const val EXTRA_ACCOUNT_EMAIL = "account_email"
        const val EXTRA_PRODUCT_NAME = "product_name"
        const val EXTRA_DAYS_REMAINING = "days_remaining"
    }
}
