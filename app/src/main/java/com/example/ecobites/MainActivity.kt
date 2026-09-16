package com.example.ecobites

import android.content.Intent
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecobites.adapter.ProductAdapter
import com.example.ecobites.model.SessionStore
import com.example.ecobites.model.UserSession

class MainActivity : AppCompatActivity() {
    companion object { const val EXTRA_NOMBRE_USUARIO = "extra_nombre_usuario"; const val EXTRA_CORREO_USUARIO = "extra_correo_usuario" }
    private lateinit var store: UserStore
    private lateinit var adapter: ProductAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_main)
        val savedSession = SessionStore(this).current()
        val name = intent.getStringExtra(EXTRA_NOMBRE_USUARIO).orEmpty().ifBlank { savedSession?.name ?: "Usuario" }
        val email = intent.getStringExtra(EXTRA_CORREO_USUARIO).orEmpty().ifBlank { savedSession?.email ?: name }
        SessionStore(this).save(UserSession(name, email, savedSession?.provider ?: "email"))
        store = UserStore(this, email)
        requestNotificationPermission()
        AppCompatDelegate.setDefaultNightMode(if (store.darkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        findViewById<TextView>(R.id.tv_saludo_usuario).text = "Hola, $name"
        store.products().also { products ->
            products.forEach { ExpiryAlertScheduler.schedule(this, it, email) }
            adapter = ProductAdapter(products.sortedBy { it.expirationMillis }.toMutableList())
        }
        findViewById<RecyclerView>(R.id.rv_lista_productos).apply { layoutManager = LinearLayoutManager(this@MainActivity); adapter = this@MainActivity.adapter }
        findViewById<Button>(R.id.btn_agregar_producto).setOnClickListener { navigate(AddProductActivity::class.java) }
        findViewById<Button>(R.id.btn_ir_productos).setOnClickListener { navigate(ProductsActivity::class.java) }
        findViewById<Button>(R.id.btn_ir_configuracion).setOnClickListener { navigate(MenuActivity::class.java) }
        findViewById<Button>(R.id.btn_ir_escaner).setOnClickListener { navigate(ScannerActivity::class.java) }
        findViewById<EditText>(R.id.et_buscar_producto).doAfterTextChanged { updateProducts(it?.toString().orEmpty()) }
    }
    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) updateProducts(findViewById<EditText>(R.id.et_buscar_producto).text.toString())
    }

    private fun updateProducts(query: String) {
        val normalizedQuery = query.trim().lowercase()
        val products = store.products().sortedBy { it.expirationMillis }.filter { product ->
            normalizedQuery.isBlank() || listOf(product.name, product.category, product.quantity)
                .any { it.lowercase().contains(normalizedQuery) }
        }
        adapter.replace(products)
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            android.widget.Toast.makeText(this, "Activa las notificaciones para recibir avisos de vencimiento.", android.widget.Toast.LENGTH_LONG).show()
        }
    }
    private fun navigate(destination: Class<*>) = startActivity(Intent(this, destination).apply { intent.extras?.let { putExtras(it) } })
}
