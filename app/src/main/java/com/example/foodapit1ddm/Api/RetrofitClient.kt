package com.example.foodapit1ddm.Api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY}
    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private const val BASE_URL = "https://platform.fatsecret.com/"

    val instance: FatSecretApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(FatSecretApi::class.java)
    }
}
