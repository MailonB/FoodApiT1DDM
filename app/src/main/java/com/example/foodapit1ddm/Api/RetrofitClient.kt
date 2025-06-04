package com.example.foodapit1ddm.Api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://platform.fatsecret.com/"
    val client: OkHttpClient = OkHttpClient.Builder()
        .build()
    val instance: FatSecretApi by lazy {
        
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(FatSecretApi::class.java)
    }
}
