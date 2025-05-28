package com.example.foodapit1ddm

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodapit1ddm.Api.AuthClient
import com.example.foodapit1ddm.Api.AuthResponse
import com.example.foodapit1ddm.Api.RetrofitClient
import com.example.foodapit1ddm.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView


        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        getAccessToken()
    }

    private fun getAccessToken() {
        AuthClient.api.getAccessToken(
            clientId = "5930ba22b39d4ec88d24a5a984f49df9",
            clientSecret = "7b00b57476ec4e768199378296bafbf7"
        ).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.access_token
                    Log.d("TOKEN", "Access Token: $token")
                    if (token != null) {
                        RetrofitClient.instance.searchFoods(
                            auth = "Bearer $token",
                            query = "batata"
                        )
                    }
                } else {
                    Log.e("TOKEN", "Erro ao obter token: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.e("TOKEN", "Falha na autenticação: ${t.message}")
            }
        })
    }
}
