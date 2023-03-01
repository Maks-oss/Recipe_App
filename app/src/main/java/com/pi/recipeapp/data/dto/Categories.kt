package com.pi.recipeapp.data.dto

data class Categories(
    val meals: List<MealCategory>
)
data class MealCategory(
    val strCategory: String
)