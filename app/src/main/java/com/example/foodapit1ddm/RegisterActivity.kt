package com.example.foodapit1ddm.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapit1ddm.databinding.ActivityLoginBinding
import com.example.foodapit1ddm.databinding.ActivityRegisterBinding
import com.example.foodapit1ddm.ui.recipes.RecipesFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp



class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Verifica se um usuário já está logado ao iniciar a Activity
        if (auth.currentUser != null) {
            Log.d("RegisterActivity", "Usuário já logado: ${auth.currentUser?.email}")
            navigateToRecipesActivity()
            return
        }


        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim() // Agora é etEmail
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

     binding.tvCreateAccount.setOnClickListener {
//            // Opção 1 (Recomendada): Navegar para uma nova RegisterActivity
//            val intent = Intent(this, CreateAccountActivity::class.java) // Crie uma nova Activity para registro
//            startActivity(intent)
         }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.etPassword.isEnabled = !isLoading
        binding.tvCreateAccount.isEnabled = !isLoading
    }

  // Firebase
    private fun loginUser(email: String, password: String) {
        showLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Log.d("RegisterActivity", "Login bem-sucedido!")
                    Toast.makeText(this, "Login bem-sucedido! Bem-vindo(a) de volta!", Toast.LENGTH_SHORT).show()
                    navigateToRecipesActivity() // Redireciona para a tela principal
                } else {
                    Log.w("RegisterActivity", "Falha no login", task.exception)
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
            .document(uid) // Usa o UID do Firebase Auth como o ID do documento
            .set(userProfile)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Perfil do usuário salvo no Firestore para UID: $uid")
            }
            .addOnFailureListener { e ->
                Log.e("RegisterActivity", "Erro ao salvar perfil do usuário no Firestore", e)
            }
    }

    // --- Método de Navegação ---
    private fun navigateToRecipesActivity() {
        val intent = Intent(this, RecipesFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
