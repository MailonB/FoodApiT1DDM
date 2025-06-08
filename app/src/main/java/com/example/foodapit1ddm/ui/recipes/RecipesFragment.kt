package com.example.foodapit1ddm.ui.recipes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodapit1ddm.Api.AuthClient
import com.example.foodapit1ddm.Api.AuthResponse
import com.example.foodapit1ddm.Api.Food
import com.example.foodapit1ddm.Api.RetrofitClient
import com.example.foodapit1ddm.databinding.FragmentRecipesBinding
import com.example.foodapit1ddm.model.RecipeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.foodapit1ddm.Api.SearchResponse as ApiSearchResponse

class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeAdapter: RecipeAdapter
    private val currentFoodList = mutableListOf<Food>()

    private var currentAccessToken: String? = null // Para armazenar o token temporariamente

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListView()
        setupSearchButton()
     }

    private fun setupListView() {
        recipeAdapter = RecipeAdapter(requireContext(), currentFoodList)
        binding.lvRecipes.adapter = recipeAdapter

        binding.lvRecipes.setOnItemClickListener { _, _, position, _ ->
        if (position < currentFoodList.size) {
            } else {
                Log.e("RecipesFragment", "Posição inválida clicada na ListView: $position, Tamanho da lista: ${currentFoodList.size}")
            }
        }
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            val searchTerm = binding.etSearch.text.toString().trim()
            if (searchTerm.isNotEmpty()) {

                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)

               if (currentAccessToken != null) {
                    searchFoodsApi(currentAccessToken!!, searchTerm)
                } else {
                    getAccessTokenAndSearch(searchTerm)
                }
            } else {
                Toast.makeText(requireContext(), "Digite um termo de busca", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAccessTokenAndSearch(searchTerm: String) {
        val CLIENT_ID = "5930ba22b39d4ec88d24a5a984f49df9"
         val CLIENT_SECRET = "7b00b57476ec4e768199378296bafbf7"
        showLoading(true)
        AuthClient.api.getAccessToken(
            clientId = CLIENT_ID,
            clientSecret = CLIENT_SECRET)
                .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful) {
                        val accessToken = response.body()?.access_token
                        if (accessToken != null) {
                            currentAccessToken = accessToken
                            Log.d("RecipesFragment", "Access Token Obtido: $accessToken")
                            searchFoodsApi(accessToken, searchTerm)
                        } else {
                            Log.e("RecipesFragment", "Access token ausente na resposta de autenticação")
                            Toast.makeText(requireContext(), "Erro ao obter token (token nulo)", Toast.LENGTH_SHORT).show()
                       }
                    } else {
                        val errorMsg = "Erro na autenticação: ${response.code()}"
                        Log.e("RecipesFragment", "$errorMsg - ${response.message()}")
                        try {
                            Log.e("RecipesFragment", "Corpo do erro de autenticação: ${response.errorBody()?.string()}")
                        } catch (e: Exception) { Log.e("RecipesFragment", "Erro ao ler corpo do erro de auth", e) }
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                      }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("RecipesFragment", "Falha na chamada de autenticação: ${t.message}", t)
                    Toast.makeText(requireContext(), "Falha na autenticação", Toast.LENGTH_SHORT).show()
                  }
            })
    }

    private fun searchFoodsApi(accessToken: String, searchTerm: String) {
        Log.d("RecipesFragment", "Buscando por: '$searchTerm'")
        RetrofitClient.instance.searchFoods(
            auth = "Bearer $accessToken",
            query = searchTerm
        ).enqueue(object : Callback<ApiSearchResponse> {
            override fun onResponse(call: Call<ApiSearchResponse>, response: Response<ApiSearchResponse>) {
                if (response.isSuccessful) {
                    showLoading(false)
                    val foodsFound = response.body()?.foods?.food
                    if (foodsFound != null && foodsFound.isNotEmpty()) {
                        Log.d("RecipesFragment", "Alimentos encontrados: ${foodsFound.size}")
                        currentFoodList.clear()
                        currentFoodList.addAll(foodsFound)
                        recipeAdapter.notifyDataSetChanged()
                      } else {
                        Log.d("RecipesFragment", "Nenhum alimento encontrado para: '$searchTerm'")
                        currentFoodList.clear()
                        recipeAdapter.notifyDataSetChanged()
                        }
                } else {
                    val errorMsg = "Erro na busca: ${response.code()}"
                    Log.e("RecipesFragment", "$errorMsg - ${response.message()}")
                    try {
                        Log.e("RecipesFragment", "Corpo do erro da busca: ${response.errorBody()?.string()}")
                    } catch (e: Exception) { Log.e("RecipesFragment", "Erro ao ler corpo do erro da busca", e) }
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                    currentFoodList.clear()
                    recipeAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ApiSearchResponse>, t: Throwable) {
                Log.e("RecipesFragment", "Falha na busca de alimentos: ${t.message}", t)
                Toast.makeText(requireContext(), "Falha na busca", Toast.LENGTH_SHORT).show()
                currentFoodList.clear()
                recipeAdapter.notifyDataSetChanged()
               }
        })

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSearch.isEnabled = !isLoading
        binding.etSearch.isEnabled = !isLoading
        binding.lvRecipes.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}