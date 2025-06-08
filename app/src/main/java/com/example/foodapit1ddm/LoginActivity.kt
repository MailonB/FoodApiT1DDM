
package com.example.foodapit1ddm.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapit1ddm.databinding.ActivityLoginBinding
import com.example.foodapit1ddm.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val createdAt: Timestamp? = null
)

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (auth.currentUser != null) {
            Log.d("LoginActivity", "Usuário já logado: ${auth.currentUser?.email}")
            navigateToMainActivity()
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.etEmail.error = "O e-mail não pode estar vazio"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "A senha não pode estar vazia"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        // tem que fazer a tela de cadastro
//        binding.tvCreateAccount.setOnClickListener {
//            // Inicia a CreateAccountActivity para o fluxo de registro
//            val intent = Intent(this, CreateAccountActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun showLoading(isLoading: Boolean) {
        // colocar o loading na tela de login
        binding.btnLogin.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.tvCreateAccount.isEnabled = !isLoading
    }

    private fun loginUser(email: String, password: String) {
        showLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "Login bem-sucedido!")
                    Toast.makeText(this, "Login bem-sucedido! Bem-vindo(a) de volta!", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                } else {
                    Log.w("LoginActivity", "Falha no login", task.exception)
                    val errorMessage = when (task.exception) {
                        is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Usuário não encontrado ou desativado."
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Credenciais inválidas. Verifique seu e-mail e senha."
                        else -> "Falha no login: ${task.exception?.message}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

  private fun saveUserProfileToFirestore(uid: String, email: String, name: String) {
        val userProfile = UserProfile(
            uid = uid,
            email = email,
            name = name,
            createdAt = Timestamp.now()
        )

        db.collection("users")
            .document(uid)
            .set(userProfile)
            .addOnSuccessListener {
                Log.d("LoginActivity", "Perfil do usuário salvo no Firestore para UID: $uid")
            }
            .addOnFailureListener { e ->
                Log.e("LoginActivity", "Erro ao salvar perfil do usuário no Firestore", e)
            }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish() }
}
