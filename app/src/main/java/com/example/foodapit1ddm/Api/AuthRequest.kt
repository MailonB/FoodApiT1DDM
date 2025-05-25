package com.example.foodapit1ddm.Api

data class AuthRequest(
    val grant_type: String = "client_credentials",
    val scope: String = "basic",
    val client_id: String,
    val client_secret: String
)