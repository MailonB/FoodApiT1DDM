package com.example.foodapit1ddm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val name: String,
    val nutrition: String
) : Parcelable
