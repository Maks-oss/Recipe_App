package com.pi.recipeapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pi.recipeapp.data.dto.Categories
import com.pi.recipeapp.room.entity.Category
import com.pi.recipeapp.room.entity.Ingredient

@Database(entities = [Ingredient::class, Category::class], version = 3)
abstract class RecipesDatabase:RoomDatabase() {
    abstract fun recipesDao():RecipesDao
}