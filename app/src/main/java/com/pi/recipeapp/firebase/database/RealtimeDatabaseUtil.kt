package com.pi.recipeapp.firebase.database


import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.pi.recipeapp.data.domain.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object RealtimeDatabaseUtil {
    private const val TAG = "RealtimeDatabase"
    lateinit var databaseReference: DatabaseReference

    fun addUserRecipeToDb(userId: String, recipe: Recipe) {
        databaseReference.child("users/${userId}/recipes").setValue(recipe)
    }

    suspend fun getUserRecipes(userId: String): List<Recipe> = withContext(Dispatchers.Default) {
        var value: List<Recipe>? = null
        while (value == null) {
            databaseReference.child("users/${userId}/recipes").get().addOnSuccessListener {
                Log.d(TAG, "getUserRecipesSuccess: ${it.value}")
                value = it.value as List<Recipe>
            }.addOnFailureListener {
                Log.w(TAG, "getUserRecipesFailed: ${it.message}")
                value = emptyList()
            }
        }
        return@withContext value!!
    }
}