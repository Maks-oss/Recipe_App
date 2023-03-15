package com.pi.recipeapp.data.domain

data class Recipe(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val instruction: String = "",
    val videoLink: String = "",
    val area: String = "",
    val category: String = "",
    val ingredients: Map<String,String> = emptyMap(),
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "imageUrl" to imageUrl,
            "instruction" to instruction,
            "videoLink" to videoLink,
            "area" to area,
            "category" to category,
            "ingredients" to ingredients
        )
    }
}