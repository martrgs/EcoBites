package com.example.ecobites

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class welcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        findViewById<Button>(R.id.btn_ir_inicio_sesion).setOnClickListener { abrirFormulario(RegisterActivity.MODO_INICIO) }
        findViewById<Button>(R.id.btn_ir_registro).setOnClickListener { abrirFormulario(RegisterActivity.MODO_REGISTRO) }
    }

    private fun abrirFormulario(modo: String) {
        startActivity(Intent(this, RegisterActivity::class.java).putExtra(RegisterActivity.EXTRA_MODO, modo))
    }
}
