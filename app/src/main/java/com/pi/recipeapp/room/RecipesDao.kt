package com.pi.recipeapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.pi.recipeapp.room.entity.Ingredient
import com.pi.recipeapp.room.entity.RecipeEntity
import com.pi.recipeapp.room.entity.RecipeWithIngredients
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {
    @Transaction
    @Query("SELECT * FROM RecipeEntity WHERE `query` = :query")
    suspend fun getAllRecipes(query: String): List<RecipeWithIngredients>

    @Query("SELECT COUNT(*) FROM RecipeEntity WHERE `query` = :query")
    suspend fun getQueryCount(query: String):Int

    @Insert
    suspend fun insertRecipes(recipes:List<RecipeEntity>)

    @Insert
    suspend fun insertRecipe(recipe:RecipeEntity)

    @Insert
    suspend fun insertIngredients(recipes:List<Ingredient>)

}