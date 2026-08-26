package com.example.ecobites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_MODO = "extra_modo_formulario"
        const val MODO_INICIO = "inicio"
        const val MODO_REGISTRO = "registro"
    }

    private lateinit var pantallaInicio: LinearLayout
    private lateinit var pantallaRegistro: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        pantallaInicio = findViewById(R.id.ll_inicio_sesion)
        pantallaRegistro = findViewById(R.id.ll_registro)

        if (intent.getStringExtra(EXTRA_MODO) == MODO_REGISTRO) mostrarRegistro() else mostrarInicio()

        findViewById<Button>(R.id.btn_volver_registro).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tv_ir_registro).setOnClickListener { mostrarRegistro() }
        findViewById<TextView>(R.id.tv_ir_inicio).setOnClickListener { mostrarInicio() }

        findViewById<Button>(R.id.btn_iniciar).setOnClickListener {
            val nombre = findViewById<EditText>(R.id.et_nombre_inicio)
            val correo = findViewById<EditText>(R.id.et_correo_inicio)
            val contrasena = findViewById<EditText>(R.id.et_contrasena_inicio)
            if (validarNombre(nombre) && validarCorreo(correo) && validarContrasena(contrasena)) {
                abrirMain(nombre.text.toString().trim())
            }
        }

        findViewById<Button>(R.id.btn_crear_cuenta).setOnClickListener {
            val nombre = findViewById<EditText>(R.id.et_nombre_registro)
            val correo = findViewById<EditText>(R.id.et_correo_registro)
            val contrasena = findViewById<EditText>(R.id.et_contrasena_registro)
            val confirmar = findViewById<EditText>(R.id.et_confirmar_contrasena)
            val aceptaTerminos = findViewById<CheckBox>(R.id.cb_terminos)
            val datosValidos = validarNombre(nombre) && validarCorreo(correo) && validarContrasena(contrasena) && validarConfirmacion(contrasena, confirmar)

            if (!aceptaTerminos.isChecked) {
                Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
            } else if (datosValidos) {
                abrirMain(nombre.text.toString().trim())
            }
        }
    }

    private fun mostrarInicio() {
        pantallaInicio.visibility = View.VISIBLE
        pantallaRegistro.visibility = View.GONE
    }

    private fun mostrarRegistro() {
        pantallaInicio.visibility = View.GONE
        pantallaRegistro.visibility = View.VISIBLE
    }

    private fun validarNombre(campo: EditText): Boolean {
        val texto = campo.text.toString().trim()
        val regex = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+(?:\\s+[A-Za-zÁÉÍÓÚáéíóúÑñ]+)+$")
        return cuando(texto.isEmpty(), !texto.matches(regex), campo, "El nombre es obligatorio", "Escribe nombre y apellido; sin números ni símbolos")
    }

    private fun validarCorreo(campo: EditText): Boolean {
        val texto = campo.text.toString().trim()
        val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return cuando(texto.isEmpty(), !texto.matches(regex), campo, "El correo es obligatorio", "Escribe un correo válido")
    }

    private fun validarContrasena(campo: EditText): Boolean {
        val texto = campo.text.toString()
        val regex = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$")
        return cuando(texto.isEmpty(), !texto.matches(regex), campo, "La contraseña es obligatoria", "Mínimo 8 caracteres: mayúscula, minúscula y número")
    }

    private fun cuando(vacio: Boolean, invalido: Boolean, campo: EditText, errorVacio: String, errorInvalido: String): Boolean {
        if (vacio) { campo.error = errorVacio; return false }
        if (invalido) { campo.error = errorInvalido; return false }
        return true
    }

    private fun validarConfirmacion(contrasena: EditText, confirmar: EditText): Boolean {
        if (confirmar.text.toString().isEmpty()) { confirmar.error = "Confirma tu contraseña"; return false }
        if (contrasena.text.toString() != confirmar.text.toString()) { confirmar.error = "Las contraseñas no coinciden"; return false }
        return true
    }

    private fun abrirMain(nombre: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_NOMBRE_USUARIO, nombre)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
