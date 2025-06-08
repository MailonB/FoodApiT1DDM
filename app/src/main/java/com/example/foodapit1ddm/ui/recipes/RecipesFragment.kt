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
import com.example.foodapit1ddm.Api.RetrofitClient
import com.example.foodapit1ddm.model.RecipeResponse
import com.example.foodapit1ddm.model.RecipeSearchResponse

import com.example.foodapit1ddm.databinding.FragmentRecipesBinding
import com.example.foodapit1ddm.model.RecipeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeAdapter: RecipeAdapter
    private val currentRecipeList = mutableListOf<RecipeResponse>()

    private var currentAccessToken: String? = null

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
        recipeAdapter = RecipeAdapter(requireContext(), currentRecipeList)
        binding.lvRecipes.adapter = recipeAdapter

        binding.lvRecipes.setOnItemClickListener { _, _, position, _ ->
            if (position < currentRecipeList.size) {
                val clickedRecipe = currentRecipeList[position]
                Log.d("RecipesFragment", "Clicou na receita: ${clickedRecipe.recipe_name}")

            } else {
                Log.e("RecipesFragment", "Posição inválida clicada na ListView: $position, Tamanho da lista: ${currentRecipeList.size}")
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
                    searchRecipesApi(currentAccessToken!!, searchTerm)
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
                            searchRecipesApi(accessToken, searchTerm)
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

    private fun searchRecipesApi(accessToken: String, searchTerm: String) {
        Log.d("RecipesFragment", "Buscando receitas por: '$searchTerm'")
        RetrofitClient.instance.searchRecipes(
            auth = "Bearer $accessToken",
            query = searchTerm
        ).enqueue(object : Callback<RecipeSearchResponse> {
            override fun onResponse(call: Call<RecipeSearchResponse>, response: Response<RecipeSearchResponse>) { // ATENÇÃO AQUI: o tipo do Response deve ser RecipeSearchResponse
                showLoading(false)
                if (response.isSuccessful) {

                    val recipesFound = response.body()?.recipes?.recipe
                    if (recipesFound != null && recipesFound.isNotEmpty()) {
                        Log.d("RecipesFragment", "Receitas encontradas: ${recipesFound.size}")
                        currentRecipeList.clear()
                        currentRecipeList.addAll(recipesFound)
                        recipeAdapter.notifyDataSetChanged()
                    } else {
                        Log.d("RecipesFragment", "Nenhuma receita encontrada para: '$searchTerm'")
                        currentRecipeList.clear()
                        recipeAdapter.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Nenhuma receita encontrada.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorMsg = "Erro na busca de receitas: ${response.code()}"
                    Log.e("RecipesFragment", "$errorMsg - ${response.message()}")
                    try {
                        Log.e("RecipesFragment", "Corpo do erro da busca de receitas: ${response.errorBody()?.string()}")
                    } catch (e: Exception) { Log.e("RecipesFragment", "Erro ao ler corpo do erro de busca de receitas", e) }
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                    currentRecipeList.clear()
                    recipeAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<RecipeSearchResponse>, t: Throwable) {
                showLoading(false)
                Log.e("RecipesFragment", "Falha na busca de receitas: ${t.message}", t)
                Toast.makeText(requireContext(), "Falha na busca de receitas", Toast.LENGTH_SHORT).show()
                currentRecipeList.clear()
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