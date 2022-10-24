package com.pi.recipeapp.repository

import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.utils.Response

interface RecipeRepository {
    suspend fun fetchMeals(query: String): Response<List<Recipe>>
}