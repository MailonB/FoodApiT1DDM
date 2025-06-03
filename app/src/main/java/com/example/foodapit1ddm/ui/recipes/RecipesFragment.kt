package com.example.foodapit1ddm.ui.recipes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.foodapit1ddm.Api.AuthClient
import com.example.foodapit1ddm.Api.AuthResponse
import com.example.foodapit1ddm.Api.RetrofitClient
import com.example.foodapit1ddm.R
import com.example.foodapit1ddm.databinding.FragmentRecipesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    data class Recipe(val name: String, val nutrition: String)

    private val allRecipes = listOf(
        Recipe("Batata Frita", "Por 100g - Cal: 86kcal | Gord: 0,05g | Carbs: 20,12g | Prot: 1,57g"),
        Recipe("Purê de Batata", "Por 100g - Cal: 90kcal | Gord: 0,1g | Carbs: 18g | Prot: 2g"),
        Recipe("Batata Assada", "Por 100g - Cal: 80kcal | Gord: 0,02g | Carbs: 17g | Prot: 1,5g"),
        Recipe("Sopa de Batata", "Por 100g - Cal: 60kcal | Gord: 0,03g | Carbs: 14g | Prot: 1g")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val listView = binding.lvRecipes
        val searchButton = binding.btnSearch
        val editText = binding.etSearch

        fun updateList(query: String) {
            val filtered = allRecipes.filter {
                it.name.contains(query, ignoreCase = true)
            }

            val adapter = object : ArrayAdapter<Recipe>(
                requireContext(),
                R.layout.item_recipe,
                R.id.tvRecipeName,
                filtered
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val row = layoutInflater.inflate(R.layout.item_recipe, parent, false)
                    val recipe = filtered[position]
                    row.findViewById<TextView>(R.id.tvRecipeName).text = recipe.name
                    row.findViewById<TextView>(R.id.tvNutritionalInfo).text = recipe.nutrition
                    return row
                }
            }

            listView.adapter = adapter
        }

        searchButton.setOnClickListener {
            val query = editText.text.toString().trim()
            if (query.isNotEmpty()) {
                getAccessToken(query)
            } else {
                Toast.makeText(context, "Digite um nome", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
                Log.d("FOOD", "Alimentos encontrados: ${searchResponse?.foods?.food.toString()}")

            } else {
                Log.e("FOOD", "Erro ao buscar alimentos: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<com.example.foodapit1ddm.Api.SearchResponse>, t: Throwable) {
            Log.e("FOOD", "Falha na busca: ${t.message}")
        }
    })
}

