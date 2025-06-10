package com.example.foodapit1ddm.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapit1ddm.databinding.ActivityCreateAccountBinding
import com.example.foodapit1ddm.model.UserProfile
import com.example.foodapit1ddm.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()


            if (name.isEmpty()) {
                binding.etName.error = "O nome não pode estar vazio"
                binding.etName.requestFocus()
                return@setOnClickListener
            }
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
            if (password.length < 6) {
                binding.etPassword.error = "A senha deve ter pelo menos 6 caracteres"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            registerUser(name, email, password)
        }

        binding.tvLogin.setOnClickListener {

            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.etName.isEnabled = !isLoading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.tvLogin.isEnabled = !isLoading
    }

    private fun registerUser(name: String, email: String, password: String) {
        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Log.d("CreateAccountActivity", "Usuário registrado com sucesso!")
                    val user = auth.currentUser
                    user?.let { firebaseUser ->

                        saveUserProfileToFirestore(firebaseUser.uid, email, name)
                    }
                    Toast.makeText(this, "Registro bem-sucedido!", Toast.LENGTH_SHORT).show()
                    navigateToRecipesActivity()
                } else {
                    Log.w("CreateAccountActivity", "Falha no registro", task.exception)

                    val errorMessage = when (task.exception) {
                        is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "Senha muito fraca. Use pelo menos 6 caracteres."
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Endereço de e-mail inválido ou já em uso."
                        is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Este e-mail já está cadastrado. Tente fazer login."
                        else -> "Falha no registro: ${task.exception?.message}"
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
                Log.d("CreateAccountActivity", "Perfil do usuário salvo no Firestore para UID: $uid")
            }
            .addOnFailureListener { e ->
                Log.e("CreateAccountActivity", "Erro ao salvar perfil do usuário no Firestore", e)
            }
    }

    private fun navigateToRecipesActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}