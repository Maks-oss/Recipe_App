package com.pi.recipeapp.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity data class Category(
    @PrimaryKey
    val categoryId: String,
    val category: String
)
