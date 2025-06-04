package com.example.foodapit1ddm.Api

    data class SearchResponse(
        val foods: Foods
    )

    data class Foods(
        val food: List<Food>
    )

    data class Food(
        val food_id: Long,
        val food_name: String,
        val brand_name: String?,
        val food_type: String,
        val food_url: String,
        val food_description: String
    )

