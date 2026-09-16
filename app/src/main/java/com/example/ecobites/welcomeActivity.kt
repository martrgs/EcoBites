package com.example.ecobites

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.ecobites.model.SessionStore

class welcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionStore(this).current()?.let { session ->
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_NOMBRE_USUARIO, session.name)
                putExtra(MainActivity.EXTRA_CORREO_USUARIO, session.email)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
            return
        }
        setContentView(R.layout.activity_welcome)
        findViewById<Button>(R.id.btn_ir_inicio_sesion).setOnClickListener { abrirFormulario(RegisterActivity.MODO_INICIO) }
        findViewById<Button>(R.id.btn_ir_registro).setOnClickListener { abrirFormulario(RegisterActivity.MODO_REGISTRO) }
    }

    private fun abrirFormulario(modo: String) {
        startActivity(Intent(this, RegisterActivity::class.java).putExtra(RegisterActivity.EXTRA_MODO, modo))
    }
}
