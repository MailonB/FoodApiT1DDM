package com.example.foodapit1ddm.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapit1ddm.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (validateInputs(username, password)) {
                // Lógica de autenticação
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonRegister.setOnClickListener {
            // Navegar para a tela de registro
            Toast.makeText(this, "Registro não implementado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(username: String, password: String): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()
    }
}