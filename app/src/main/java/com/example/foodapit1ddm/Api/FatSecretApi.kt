package com.example.foodapit1ddm.Api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FatSecretApi {
    @GET("rest/server.api")
    fun searchFoods(
        @Header("Authorization") auth: String,
        @Query("method") method: String = "foods.search",
        @Query("search_expression") query: String,
        @Query("format") format: String = "json"
    ): Call<SearchResponse>
}

