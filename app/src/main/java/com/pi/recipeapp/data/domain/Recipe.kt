package com.pi.recipeapp.data.domain

data class Recipe(
    val id: String = "",
    var name: String = "",
    var imageUrl: String = "",
    var instruction: String = "",
    val videoLink: String = "",
    val area: String = "",
    val category: String = "",
    var ingredients: Map<String,String> = emptyMap(),
    var isSelected: Boolean = false
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