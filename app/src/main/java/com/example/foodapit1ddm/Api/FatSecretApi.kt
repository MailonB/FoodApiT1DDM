package com.example.foodapit1ddm.Api

import com.example.foodapit1ddm.model.RecipeSearchResponse // Certifique-se de que esta importação está correta
import com.example.foodapit1ddm.model.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FatSecretApi {

    @GET("rest/server.api")
    fun searchFoods(
        @Header("Authorization") auth: String,
        @Header("Accept-Language") acceptLanguage: String = "pt-BR",
        @Query("method") method: String = "foods.search",
        @Query("search_expression") query: String,
        @Query("format") format: String = "json",
        @Query("region") region: String = "BR",
        @Query("language") language: String = "pt"
    ): Call<SearchResponse>

    @GET("rest/server.api")
    fun searchRecipes(
        @Header("Authorization") auth: String,
        @Query("method") method: String = "recipes.search.v3",
        @Query("search_expression") query: String,
        @Query("format") format: String = "json",
        @Query("region") region: String = "BR",
        @Query("language") language: String = "pt"
    ): Call<RecipeSearchResponse>
}