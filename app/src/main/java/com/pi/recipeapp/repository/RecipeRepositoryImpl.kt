package com.pi.recipeapp.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.mapper.RecipesMapper
import com.pi.recipeapp.retrofit.RecipesService
import com.pi.recipeapp.firebase.utils.CloudStorageUtil
import com.pi.recipeapp.firebase.utils.FirebaseUtil
import com.pi.recipeapp.utils.Response
import kotlin.properties.Delegates

class RecipeRepositoryImpl(
    private val recipesService: RecipesService,
    private val firebaseUtil: FirebaseUtil
) : RecipeRepository {
    companion object {
        private const val TAG = "RecipeRepository"
    }

    override fun getCurrentUser(): FirebaseUser? = firebaseUtil.currentUser

    override suspend fun fetchMeals(query: String): Response<List<Recipe>> {
        return try {
            val recipes = recipesService.getRecipesByNamesResponse(query)
            if (recipes.meals.isNullOrEmpty()) {
                Response.Success(emptyList<List<Recipe>>())
            }
            Log.d(TAG, "Recipes from server")
            val recipeList = RecipesMapper.convertRecipeDtoToDomain(recipes)
            Response.Success(recipeList)
        } catch (exc: Exception) {
            Log.e(TAG, "fetchMeals: ${exc.message}")
            Response.Error(exc.message)
        }

    }

    override fun addRecipeToUserFavorites(recipe: Recipe) {
        firebaseUtil.addRecipeToDatabase(recipe)
    }

    override fun removeRecipesFromUserFavorites(recipes: List<Recipe>) {
        firebaseUtil.removeRecipesFromDatabase(recipes)
    }

    override fun addUserSavedRecipesListener(
        userId: String,
        onRecipeDataChangeCallback: (List<Recipe?>?) -> Unit
    ) {
        firebaseUtil.addSavedRecipesListener(userId,onRecipeDataChangeCallback)
    }

}