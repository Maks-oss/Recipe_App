package com.pi.recipeapp.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ingredient(
    @PrimaryKey
    val ingredientId: String,
    val recipeId: String,
    val ingredient: String,
    val measure:String
)
