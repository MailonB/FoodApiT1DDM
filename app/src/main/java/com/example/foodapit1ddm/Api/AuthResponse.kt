package com.example.foodapit1ddm.Api

data class AuthResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)
