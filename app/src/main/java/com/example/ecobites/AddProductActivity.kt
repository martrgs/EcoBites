package com.example.ecobites

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecobites.model.Product
import com.example.ecobites.model.SessionStore
import java.text.SimpleDateFormat
import java.util.Locale

class AddProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_add_product)
        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<Button>(R.id.btn_save_product).setOnClickListener {
            val name = findViewById<EditText>(R.id.et_product_name).text.toString().trim()
            val expiryText = findViewById<EditText>(R.id.et_product_days).text.toString().trim()
            val expiry = runCatching {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { isLenient = false }.parse(expiryText)?.time
            }.getOrNull()
            val quantity = findViewById<EditText>(R.id.et_quantity).text.toString().trim()
            if (name.length !in 2..50) { invalid(findViewById(R.id.et_product_name), "El nombre debe tener entre 2 y 50 caracteres"); return@setOnClickListener }
            if (expiry == null) { invalid(findViewById(R.id.et_product_days), "Usa el formato dd/mm/aaaa"); return@setOnClickListener }
            if (expiry <= System.currentTimeMillis()) { invalid(findViewById(R.id.et_product_days), "La fecha de vencimiento debe ser futura"); return@setOnClickListener }
            if (quantity.isNotEmpty() && !quantity.matches(Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9., ]{1,40}$"))) { invalid(findViewById(R.id.et_quantity), "La cantidad contiene caracteres no válidos"); return@setOnClickListener }
            val email = SessionStore(this).current()?.email
                ?: intent.getStringExtra(MainActivity.EXTRA_CORREO_USUARIO).orEmpty()
            val store = UserStore(this, email); val products = store.products()
            val product = Product(name = name, category = findViewById<Spinner>(R.id.sp_category).selectedItem.toString(), expirationMillis = expiry, quantity = quantity)
            products.add(product)
            store.saveProducts(products)
            ExpiryAlertScheduler.schedule(this, product, email)
            Toast.makeText(this, "Producto guardado. Recibirás avisos desde 5 días antes de vencer.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    private fun invalid(field: EditText, message: String) {
        field.error = message
        field.requestFocus()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
