package com.example.taller2.activities

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taller2.R

class registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_registro)
        val scrollView = findViewById<ViewGroup>(R.id.ScrollRegistro)

        ViewCompat.setOnApplyWindowInsetsListener(scrollView){ v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeIntents = insets.getInsets(WindowInsetsCompat.Type.ime())

            val bottomPading = maxOf(systemBars.bottom, imeIntents.bottom)

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                bottomPading
            )
            insets


        }


        val volver = findViewById<TextView>(R.id.VolverLogIn)
        volver.setOnClickListener {
            val intent = Intent(this, PantallaLogin::class.java)
            startActivity(intent)
            finish() // Opcional
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRegistro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}