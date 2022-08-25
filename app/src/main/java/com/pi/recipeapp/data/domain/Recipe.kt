package com.pi.recipeapp.data.domain

data class Recipe(
    val name: String,
    val imageUrl: String,
    val instruction: String = "",
    val videoLink: String = "",
    val area: String = "",
    val category: String = "",
    val ingredients: Map<String,String> = emptyMap(),
)