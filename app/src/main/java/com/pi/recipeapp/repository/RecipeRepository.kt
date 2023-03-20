package com.pi.recipeapp.repository

import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.utils.Response

interface RecipeRepository {
    suspend fun fetchMeals(query: String): Response<List<Recipe>>
    suspend fun fetchRecipeByName(name: String): Response<Recipe>
    suspend fun fetchCategoriesFromLocalDb(): List<String>
    suspend fun fetchIngredientsFromLocalDb(): List<String>
    fun addRecipeToUserFavorites(userId: String, recipe: Recipe)
    fun removeRecipesFromUserFavorites(userId: String, recipes: List<Recipe>)
    fun addUserSavedRecipesListener(userId: String, onRecipeDataChangeCallback: (List<Recipe?>?) -> Unit)
}