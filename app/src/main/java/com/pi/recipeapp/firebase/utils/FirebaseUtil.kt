package com.pi.recipeapp.firebase.utils

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.pi.recipeapp.data.domain.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.properties.Delegates

class FirebaseUtil(
    private val cloudStorageUtil: CloudStorageUtil,
    private val databaseReference: DatabaseReference
) {
    companion object {
        private const val TAG = "FirebaseUtil"
    }
    var savedRecipes: List<Recipe?>? by mutableStateOf(null)
    var currentUser by Delegates.observable(Firebase.auth.currentUser) { _, _, newValue ->
        if (newValue != null) {
            addSavedRecipesListener(newValue.uid) {
                savedRecipes = it
            }
        }
    }
    init {
        if (currentUser != null) {
            addSavedRecipesListener(currentUser!!.uid) {
                savedRecipes = it
            }
        }
    }

    fun addRecipeToDatabase(userId: String,recipe: Recipe) {
        val recipeKey = databaseReference.child("users/${userId}").push()
        Log.d(TAG, "addRecipeToDatabase: ")
        if (recipe.category.startsWith("Own")) {
            cloudStorageUtil.uploadImageToCloud(
                "recipeImages/${recipeKey.key}.jpg", Uri.parse(recipe.imageUrl), onSuccess = {
                    recipeKey.setValue(recipe.copy(imageUrl = it.toString()))
                }, onFailure = {
                    Log.e(
                        TAG,
                        "addRecipeToUserFavorites: Uploading recipe image failed ${it?.message}"
                    )
                })
        } else {
            Log.d(TAG, "addRecipeToDatabaseSuccess: ")
            recipeKey.setValue(recipe)
        }
    }

    fun removeRecipesFromDatabase(userId: String, recipes: List<Recipe>) {
        val ref = databaseReference.child("users/${userId}")
        val query = ref.orderByChild("id")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val updates = HashMap<String, Any?>()
                for (childSnapshot in dataSnapshot.children) {
                    val value = childSnapshot.getValue(Recipe::class.java)
                    if (value != null && recipes.contains(value)) {
                        updates[childSnapshot.key!!] = null
                    }
                }
                Log.d(TAG, "deleteUserRecipesSuccess: $updates")
                ref.updateChildren(updates)
                val mutableList = savedRecipes?.toMutableList()
                mutableList?.removeAll(recipes)
                savedRecipes = mutableList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "deleteUserRecipesFailed: ${databaseError.message}")
            }
        })
    }
    fun addSavedRecipesListener(userId: String,onDataChange: (List<Recipe?>?) -> Unit) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recipes = dataSnapshot.getValue<HashMap<String, Recipe?>>()
                onDataChange(recipes?.values?.toList())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadRecipes:onCancelled", databaseError.toException())
            }
        }
        databaseReference.child("users/${userId}").addValueEventListener(postListener)
    }

}