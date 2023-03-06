package com.pi.recipeapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.pi.recipeapp.room.entity.Category
import com.pi.recipeapp.room.entity.Ingredient

@Dao
interface RecipesDao {
    @Query("SELECT ingredient FROM Ingredient")
    suspend fun getIngredients(): List<String>
    @Insert
    suspend fun insertIngredients(ingredients: List<Ingredient>)

    @Query("SELECT category FROM Category")
    suspend fun getCategories(): List<String>

    @Insert
    suspend fun insertCategories(categories: List<Category>)

}