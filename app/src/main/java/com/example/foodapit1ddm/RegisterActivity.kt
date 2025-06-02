package com.example.foodapit1ddm.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapit1ddm.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Definir o comportamento do botão de cadastro
        binding.btnRegister.setOnClickListener {
            // Lógica de cadastro aqui (validar campos, salvar no banco de dados, etc.)
        }

        // o clique no "Faça o login" para voltar para LoginActivity
        binding.tvCreateAccount.setOnClickListener {
            finish()
        }
    }
}