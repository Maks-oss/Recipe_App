package com.pi.recipeapp.data.dto

data class Ingredients(
    val meals: List<Ingredient>
)
data class Ingredient(
    val idIngredient: String,
    val strDescription: String,
    val strIngredient: String,
    val strType: Any
)