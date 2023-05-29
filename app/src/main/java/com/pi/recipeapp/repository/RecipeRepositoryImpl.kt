package com.pi.recipeapp.repository

import android.content.Context
import android.graphics.Bitmap
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
import com.pi.recipeapp.ml.LiteModelAiyVisionClassifierFoodV11
import com.pi.recipeapp.utils.Response
import org.tensorflow.lite.support.image.TensorImage
import java.net.UnknownHostException
import kotlin.properties.Delegates

class RecipeRepositoryImpl(
    private val recipesService: RecipesService,
    private val firebaseUtil: FirebaseUtil,
    private val tfModel: LiteModelAiyVisionClassifierFoodV11

) : RecipeRepository {
    companion object {
        private const val TAG = "RecipeRepository"
    }

    override fun getCurrentUser(): FirebaseUser? = firebaseUtil.currentUser

    override suspend fun fetchMealsByText(query: String): Response<List<Recipe>> {
        return try {
            val recipes = recipesService.getRecipesByNamesResponse(query)
            if (recipes.meals.isNullOrEmpty()) {
                Response.Success(emptyList<List<Recipe>>())
            }
            val recipeList = RecipesMapper.convertRecipeDtoToDomain(recipes)
            Response.Success(recipeList)
        } catch (ex: UnknownHostException) {
            Log.e(TAG, "fetchMeals: ${ex.stackTraceToString()}")
            Response.Error("Could not send request to server. Please check your internet connection")
        } catch (exc: Exception) {
            Log.e(TAG, "fetchMeals: ${exc.stackTraceToString()}")
            Response.Error(exc.message)
        }

    }

    override suspend fun fetchMealsByPhoto(imageBitmap: Bitmap?): Pair<String, Response<List<Recipe>>> {
        return try {
            val mealName = getTFSearchResult(imageBitmap)
            Log.d(TAG, "fetchMealsByPhoto: $mealName")
            mealName to fetchMealsByText(mealName)
        } catch (ex: UnknownHostException) {
            Log.e(TAG, "fetchMeals: ${ex.stackTraceToString()}")
            "" to Response.Error("Could not send request to server. Please check your internet connection")
        } catch (e: Exception) {
            Log.d(TAG, "fetchMealsByPhotoerror: ${e.message}")
            "" to Response.Error(e.message)
        }
    }

    override fun addRecipeToUserFavorites(userId: String,recipe: Recipe) {
        firebaseUtil.addRecipeToDatabase(userId,recipe)
    }

    override fun removeRecipesFromUserFavorites(userId: String, recipes: List<Recipe>) {
        firebaseUtil.removeRecipesFromDatabase(userId, recipes)
    }

    override fun addUserSavedRecipesListener(
        userId: String,
        onRecipeDataChangeCallback: (List<Recipe?>?) -> Unit
    ) {
        firebaseUtil.addSavedRecipesListener(userId,onRecipeDataChangeCallback)
    }

    override fun getSavedRecipes(): List<Recipe?>? = firebaseUtil.savedRecipes

    private fun getTFSearchResult(
        imageBitmap: Bitmap?,
    ): String {
        var recipeName = ""
        if (imageBitmap != null) {
            val image = TensorImage.fromBitmap(
                imageBitmap
            )
            val outputs = tfModel.process(image)
            val probability = outputs.probabilityAsCategoryList
            val maxProbabilityItem = probability.maxByOrNull { it.score }
            val score = maxProbabilityItem?.score ?: 0
            if (score.toDouble() < 0.5) {
                throw Exception("Unrecognized image. Please provide more detailed image")
            }
            val recipe = maxProbabilityItem?.label ?: ""
            recipeName = recipe
//            tfModel.close()
        }
        return recipeName

    }

}