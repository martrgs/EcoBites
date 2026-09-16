package com.example.ecobites

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ScannerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContentView(R.layout.activity_scanner)
        findViewById<Button>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<Button>(R.id.btn_scan_placeholder).setOnClickListener { Toast.makeText(this, "El escáner requiere integrar CameraX/ML Kit y permiso de cámara.", Toast.LENGTH_LONG).show() }
    }
}
