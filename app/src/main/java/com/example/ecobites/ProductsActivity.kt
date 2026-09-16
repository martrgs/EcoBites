package com.example.ecobites

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecobites.adapter.ProductAdapter
import com.example.ecobites.model.SessionStore

class ProductsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.activity_products)
        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }
        val email = SessionStore(this).current()?.email
            ?: intent.getStringExtra(MainActivity.EXTRA_CORREO_USUARIO).orEmpty()
        findViewById<RecyclerView>(R.id.rv_all_products).apply { layoutManager = LinearLayoutManager(this@ProductsActivity); adapter = ProductAdapter(UserStore(this@ProductsActivity, email).products().sortedBy { it.expirationMillis }.toMutableList()) }
    }
}
