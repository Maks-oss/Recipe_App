package com.pi.recipeapp.repository

import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseUser
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.utils.Response

interface RecipeRepository {
    fun getCurrentUser(): FirebaseUser?
    suspend fun fetchMealsByText(query: String): Response<List<Recipe>>
    suspend fun fetchMealsByPhoto(imageBitmap: Bitmap?): Pair<String, Response<List<Recipe>>>
    fun addRecipeToUserFavorites(userId: String, recipe: Recipe)
    fun removeRecipesFromUserFavorites(userId: String,recipes: List<Recipe>)
    fun addUserSavedRecipesListener(userId: String,onRecipeDataChangeCallback: (List<Recipe?>?) -> Unit)
    fun getSavedRecipes(): List<Recipe?>?
}