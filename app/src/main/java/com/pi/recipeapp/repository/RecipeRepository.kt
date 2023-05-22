package com.pi.recipeapp.repository

import com.google.firebase.auth.FirebaseUser
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.utils.Response

interface RecipeRepository {
    fun getCurrentUser(): FirebaseUser?
    suspend fun fetchMeals(query: String): Response<List<Recipe>>
    fun addRecipeToUserFavorites( recipe: Recipe)
    fun removeRecipesFromUserFavorites(recipes: List<Recipe>)
    fun addUserSavedRecipesListener(userId: String,onRecipeDataChangeCallback: (List<Recipe?>?) -> Unit)

}