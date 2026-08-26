package com.example.ecobites

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NOMBRE_USUARIO = "extra_nombre_usuario"
    }

    private lateinit var btnAgregarProducto: FloatingActionButton
    private lateinit var cardFormularioAgregar: CardView
    private lateinit var btnGuardarProducto: Button
    private lateinit var etNombreProducto: EditText
    private lateinit var etDiasVencimiento: EditText
    private lateinit var etBuscarProducto: EditText
    private lateinit var layoutListaProductos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAgregarProducto = findViewById(R.id.btn_agregar_producto)
        cardFormularioAgregar = findViewById(R.id.cv_formulario_agregar)
        btnGuardarProducto = findViewById(R.id.btn_guardar_producto)
        etNombreProducto = findViewById(R.id.et_nombre_producto)
        etDiasVencimiento = findViewById(R.id.et_dias_vencimiento)
        etBuscarProducto = findViewById(R.id.et_buscar_producto)
        layoutListaProductos = findViewById(R.id.ll_lista_productos)

        findViewById<Button>(R.id.btn_volver_main).setOnClickListener {
            finish()
        }

        val nombreUsuario = intent.getStringExtra(EXTRA_NOMBRE_USUARIO).orEmpty()
        if (nombreUsuario.isNotBlank()) {
            findViewById<TextView>(R.id.tv_saludo_usuario).text = "¡Bienvenido, $nombreUsuario!"
        }

        // Alternar visualización del formulario
        btnAgregarProducto.setOnClickListener {
            if (cardFormularioAgregar.visibility == View.GONE) {
                cardFormularioAgregar.visibility = View.VISIBLE
            } else {
                cardFormularioAgregar.visibility = View.GONE
            }
        }

        // Guardar producto
        btnGuardarProducto.setOnClickListener {
            val nombre = etNombreProducto.text.toString().trim()
            val diasTexto = etDiasVencimiento.text.toString().trim()

            if (validarCampos(nombre, diasTexto)) {
                val dias = diasTexto.toInt()
                agregarTarjetaSemaforo(nombre, dias)

                etNombreProducto.setText("")
                etDiasVencimiento.setText("")
                cardFormularioAgregar.visibility = View.GONE

                Toast.makeText(this, "Producto guardado con éxito", Toast.LENGTH_SHORT).show()
            }
        }

        // Buscador
        etBuscarProducto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarProductos(s.toString().trim().lowercase())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validarCampos(nombre: String, dias: String): Boolean {
        val regexTexto = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
        val regexNumeros = Regex("^[1-9][0-9]{0,2}$")

        if (nombre.isEmpty()) {
            etNombreProducto.error = "Ingresa el nombre del producto"
            return false
        }

        if (!nombre.matches(regexTexto)) {
            etNombreProducto.error = "El nombre no puede contener números ni símbolos"
            return false
        }

        if (dias.isEmpty()) {
            etDiasVencimiento.error = "Ingresa los días de vencimiento"
            return false
        }

        if (!dias.matches(regexNumeros)) {
            etDiasVencimiento.error = "Ingresa un número entero válido entre 1 y 999"
            return false
        }

        return true
    }

    private fun agregarTarjetaSemaforo(nombre: String, dias: Int) {
        val nuevaCard = CardView(this)
        val paramsCard = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        paramsCard.setMargins(0, 0, 0, 24)
        nuevaCard.layoutParams = paramsCard
        nuevaCard.radius = 28f
        nuevaCard.cardElevation = 6f
        nuevaCard.tag = nombre.lowercase()

        val colorTextoEstado: Int
        val colorFondoCard: Int
        val mensajeEstado: String

        when {
            dias <= 3 -> {
                colorTextoEstado = Color.parseColor("#D32F2F")
                colorFondoCard = Color.parseColor("#FFEBEE")
                mensajeEstado = "Vence en: $dias días (Crítico)"
            }
            dias in 4..7 -> {
                colorTextoEstado = Color.parseColor("#F57C00")
                colorFondoCard = Color.parseColor("#FFF3E0")
                mensajeEstado = "Vence en: $dias días (Próximo)"
            }
            else -> {
                colorTextoEstado = Color.parseColor("#388E3C")
                colorFondoCard = Color.parseColor("#E8F5E9")
                mensajeEstado = "Vence en: $dias días (En buen estado)"
            }
        }

        nuevaCard.setCardBackgroundColor(colorFondoCard)

        val layoutContenido = LinearLayout(this)
        layoutContenido.orientation = LinearLayout.HORIZONTAL
        layoutContenido.setPadding(36, 32, 36, 32)
        layoutContenido.gravity = android.view.Gravity.CENTER_VERTICAL

        val imgIcono = ImageView(this)
        val paramsImg = LinearLayout.LayoutParams(84, 84)
        imgIcono.layoutParams = paramsImg
        imgIcono.setImageResource(android.R.drawable.ic_menu_gallery)
        imgIcono.setColorFilter(colorTextoEstado)

        val layoutTextos = LinearLayout(this)
        val paramsTextos = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        )
        paramsTextos.setMargins(28, 0, 0, 0)
        layoutTextos.orientation = LinearLayout.VERTICAL
        layoutTextos.layoutParams = paramsTextos

        val tvNombre = TextView(this)
        tvNombre.text = nombre
        tvNombre.textSize = 16f
        tvNombre.setTypeface(null, Typeface.BOLD)
        tvNombre.setTextColor(Color.parseColor("#1A252C"))

        val tvVencimiento = TextView(this)
        tvVencimiento.text = mensajeEstado
        tvVencimiento.textSize = 13f
        tvVencimiento.setTypeface(null, Typeface.BOLD)
        tvVencimiento.setTextColor(colorTextoEstado)

        layoutTextos.addView(tvNombre)
        layoutTextos.addView(tvVencimiento)

        val viewIndicador = View(this)
        val paramsIndicador = LinearLayout.LayoutParams(24, 24)
        viewIndicador.layoutParams = paramsIndicador
        viewIndicador.setBackgroundResource(android.R.drawable.ic_notification_overlay)
        viewIndicador.backgroundTintList = android.content.res.ColorStateList.valueOf(colorTextoEstado)

        layoutContenido.addView(imgIcono)
        layoutContenido.addView(layoutTextos)
        layoutContenido.addView(viewIndicador)

        nuevaCard.addView(layoutContenido)

        layoutListaProductos.addView(nuevaCard, 0)
    }

    private fun filtrarProductos(textoConsulta: String) {
        for (i in 0 until layoutListaProductos.childCount) {
            val vistaTarjeta = layoutListaProductos.getChildAt(i)
            val nombreProducto = vistaTarjeta.tag?.toString() ?: ""

            if (textoConsulta.isEmpty() || nombreProducto.contains(textoConsulta)) {
                vistaTarjeta.visibility = View.VISIBLE
            } else {
                vistaTarjeta.visibility = View.GONE
            }
        }
    }
}
