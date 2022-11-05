package com.pi.recipeapp.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val imageUrl: String,
    val instruction: String = "",
    val videoLink: String = "",
    val area: String = "",
    val category: String = "",
    val query: String
)