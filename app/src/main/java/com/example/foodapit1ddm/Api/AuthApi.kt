package com.example.foodapit1ddm.Api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {
    @FormUrlEncoded
    @POST("connect/token")
    fun getAccessToken(
        @Field("grant_type") grantType: String = "client_credentials",
        @Field("scope") scope: String = "basic",
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String
    ): Call<AuthResponse>

}