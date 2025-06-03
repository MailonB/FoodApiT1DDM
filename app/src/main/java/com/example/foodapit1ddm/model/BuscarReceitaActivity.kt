package com.example.foodapit1ddm.model

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.foodapit1ddm.Api.AuthClient
import com.example.foodapit1ddm.Api.AuthResponse
import com.example.foodapit1ddm.Api.RetrofitClient
import com.example.foodapit1ddm.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuscarReceitaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_recipes)

        val termo = findViewById<EditText>(R.id.etSearch)
        val buscar = findViewById<Button>(R.id.btnSearch)
        val lista = findViewById<ListView>(R.id.lvRecipes)

        buscar.setOnClickListener {
            val query = termo.text.toString()
            getAccessToken(query)
        }
    }

    private fun getAccessToken(query: String) {
        AuthClient.api.getAccessToken(
            clientId = "5930ba22b39d4ec88d24a5a984f49df9",
            clientSecret = "7b00b57476ec4e768199378296bafbf7"
        ).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.access_token
                    Log.d("TOKEN", "Access Token: $token")
                    if (token != null) {
                        searchFoods(token, query)
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

    private fun searchFoods(token: String, query: String) {
        RetrofitClient.instance.searchFoods(
            auth = "Bearer $token",
            query = query
        ).enqueue(object : Callback<com.example.foodapit1ddm.Api.SearchResponse> {
            override fun onResponse(
                call: Call<com.example.foodapit1ddm.Api.SearchResponse>,
                response: Response<com.example.foodapit1ddm.Api.SearchResponse>
            ) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    Log.d("FOOD", "Alimentos encontrados: ${searchResponse?.foods?.food}")
                    // Aqui você pode atualizar o ListView ou outra UI.
                } else {
                    Log.e("FOOD", "Erro ao buscar alimentos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<com.example.foodapit1ddm.Api.SearchResponse>, t: Throwable) {
                Log.e("FOOD", "Falha na busca: ${t.message}")
            }
        })
    }
}
