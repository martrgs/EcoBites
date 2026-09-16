package com.example.ecobites

import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.materialswitch.MaterialSwitch
import com.example.ecobites.model.SessionStore

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.activity_settings)
        val email = SessionStore(this).current()?.email
            ?: intent.getStringExtra(MainActivity.EXTRA_CORREO_USUARIO).orEmpty()
        val store = UserStore(this, email)
        val language = findViewById<Spinner>(R.id.sp_language); language.setSelection(if (store.language == "English") 1 else 0)
        val dark = findViewById<MaterialSwitch>(R.id.sw_dark_mode); val notifications = findViewById<MaterialSwitch>(R.id.sw_notifications)
        dark.isChecked = store.darkMode; notifications.isChecked = store.notifications
        dark.setOnCheckedChangeListener { _, checked -> store.darkMode = checked; AppCompatDelegate.setDefaultNightMode(if (checked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO) }
        notifications.setOnCheckedChangeListener { _, checked ->
            store.notifications = checked
            if (checked) store.products().forEach { ExpiryAlertScheduler.schedule(this, it, email) }
            else ExpiryAlertScheduler.cancelAll(this, store.products(), email)
        }
        language.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener { override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {} ; override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) { store.language = language.selectedItem.toString() } }
        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }
    }
}
