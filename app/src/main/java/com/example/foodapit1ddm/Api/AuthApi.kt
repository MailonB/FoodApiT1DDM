package com.example.foodapit1ddm.Api

import retrofit2.Call
import retrofit2.http.*

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