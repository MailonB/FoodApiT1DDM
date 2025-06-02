package com.example.foodapit1ddm.model

data class FoodItem(
    val id: Long,
    val name: String,
    val type: String,
    val brand: String?,
    val description: String
)