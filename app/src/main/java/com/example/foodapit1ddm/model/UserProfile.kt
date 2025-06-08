package com.example.foodapit1ddm.model

import com.google.firebase.Timestamp

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val createdAt: Timestamp? = null
)