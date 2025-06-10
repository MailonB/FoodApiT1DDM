package com.example.foodapit1ddm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

@Parcelize
data class RecipeResponse(
    // Todas as propriedades devem ser 'var' e ter um valor padrão 'null'
    // para que o Firestore possa criar instâncias da classe.
    var recipe_id: Long? = null,
    var recipe_name: String? = null,
    var recipe_description: String? = null,
    var recipe_image: String? = null,
    var recipe_nutrition: RecipeNutritionResponse? = null,
    var recipe_ingredients: RecipeIngredientsResponse? = null,
    var recipe_types: RecipeTypesResponse? = null
) : Parcelable

data class RecipeSearchResponse(
    val recipes: RecipesContainer
)

data class RecipesContainer(
    val max_results: Int,
    val total_results: Int,
    val page_number: Int,
    val recipe: List<RecipeResponse>?
)

@Parcelize
data class RecipeNutritionResponse(
    var calories: String? = null,
    var carbohydrate: String? = null,
    var protein: String? = null,
    var fat: String? = null
) : Parcelable

@Parcelize
data class RecipeIngredientsResponse(
    var ingredient: List<String>? = null
) : Parcelable

@Parcelize
data class RecipeTypesResponse(
    var recipe_type: List<String>? = null
) : Parcelable