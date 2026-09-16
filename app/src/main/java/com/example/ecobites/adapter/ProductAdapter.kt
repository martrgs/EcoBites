package com.example.ecobites.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecobites.R
import com.example.ecobites.model.Product
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductAdapter(private val products: MutableList<Product>) : RecyclerView.Adapter<ProductAdapter.ProductHolder>() {
    class ProductHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_product_name)
        val detail: TextView = view.findViewById(R.id.tv_product_detail)
        val status: TextView = view.findViewById(R.id.tv_product_status)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false))
    override fun getItemCount() = products.size
    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product = products[position]
        val days = product.daysUntilExpiry()
        holder.name.text = product.name
        holder.detail.text = "${product.category} · ${SimpleDateFormat("dd MMM yyyy", Locale("es")).format(Date(product.expirationMillis))}"
        val (label, color) = when { days == 0L -> "Vencido" to "#D63A32"; days <= 3 -> "Por vencer" to "#E98A17"; else -> "Vigente" to "#198754" }
        holder.status.text = label
        holder.status.setTextColor(Color.parseColor(color))
        holder.status.setBackgroundResource(when { days == 0L -> R.drawable.bg_danger; days <= 3 -> R.drawable.bg_warning; else -> R.drawable.bg_ok })
    }
    fun replace(items: List<Product>) { products.clear(); products.addAll(items); notifyDataSetChanged() }
}
