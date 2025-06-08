package com.example.foodapit1ddm.model


import com.google.gson.annotations.SerializedName

data class RecipeSearchResponse(
    val recipes: RecipesContainer
)

data class RecipesContainer(
    val max_results: Int,
    val total_results: Int,
    val page_number: Int,
    val recipe: List<RecipeResponse>?
)


data class RecipeResponse(
    val recipe_id: Long,
    val recipe_name: String,
    val recipe_description: String?,
    val recipe_image: String?,
    val recipe_nutrition: RecipeNutritionResponse?,
    val recipe_ingredients: RecipeIngredientsResponse?,
    val recipe_types: RecipeTypesResponse?
)


data class RecipeNutritionResponse(
    val calories: String?,
    val carbohydrate: String?,
    val protein: String?,
    val fat: String?
)


data class RecipeIngredientsResponse(
    val ingredient: List<String>?
)


data class RecipeTypesResponse(
    val recipe_type: List<String>?
)
