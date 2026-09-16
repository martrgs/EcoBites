package com.example.ecobites

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ecobites.model.SessionStore

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        findViewById<View>(R.id.menu_home).setOnClickListener { finish() }
        findViewById<View>(R.id.menu_products).setOnClickListener { open(ProductsActivity::class.java) }
        findViewById<View>(R.id.menu_settings).setOnClickListener { open(SettingsActivity::class.java) }
        findViewById<View>(R.id.menu_logout).setOnClickListener {
            SessionStore(this).clear()
            startActivity(Intent(this, welcomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finishAffinity()
        }
    }

    private fun open(destination: Class<*>) =
        startActivity(Intent(this, destination).apply { intent.extras?.let { putExtras(it) } })
}
