package com.example.foodapit1ddm.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapit1ddm.R
import com.example.foodapit1ddm.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Link para a tela de cadastro
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)
        tvCreateAccount.setOnClickListener {
            // Abrir a tela de cadastro
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Lógica de login e outros elementos aqui (se necessário)
    }
}
