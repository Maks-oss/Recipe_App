package com.pi.recipeapp.room

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase
import com.pi.recipeapp.room.entity.Ingredient
import com.pi.recipeapp.room.entity.RecipeEntity

@Database(entities = [RecipeEntity::class,Ingredient::class], version = 1)
abstract class RecipesDatabase:RoomDatabase() {
    abstract fun recipesDao():RecipesDao
}