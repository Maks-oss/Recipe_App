package com.pi.recipeapp.repository

import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.utils.Response
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    suspend fun fetchMeals(query: String): Response<List<Recipe>>
    suspend fun fetchRecipeByName(name: String): Response<Recipe>
    suspend fun fetchCategories(): List<String>
    suspend fun fetchIngredients(): List<String>
//    suspend fun fetchMealsFromDatabase(query: String): Flow<Recipe>
}