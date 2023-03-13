package com.pi.recipeapp.firebase.database


import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.pi.recipeapp.data.domain.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object RealtimeDatabaseUtil {
    private const val TAG = "RealtimeDatabase"
    lateinit var databaseReference: DatabaseReference
    var isListenerAdded: Boolean = false
        private set
    fun addUserRecipeToDb(userId: String, recipe: Recipe) {
        val reference = databaseReference.child("users/${userId}")
        reference.get().addOnSuccessListener {
            Log.d(TAG, "getUserRecipesSuccess: ${it.value?.javaClass?.simpleName}")
            val recipes = it.value as? List<Recipe>
            if (recipes != null) {
                val newListData = recipes.toMutableList()
                if (!newListData.contains(recipe)) {
                    reference.setValue(newListData.apply { add(recipe) })
                } else {
                    // Display message about existing recipe
                }
            } else {
                reference.setValue(listOf(recipe))
            }
        }.addOnFailureListener {
            Log.w(TAG, "getUserRecipesFailed: ${it.message}")
        }
    }

    fun addUserRecipesListener(userId: String, onRecipeDataChange: (List<Recipe>?) -> Unit) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val recipes = dataSnapshot.getValue<List<Recipe>>()
                onRecipeDataChange(recipes)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        databaseReference.child("users/${userId}").addValueEventListener(postListener)

    }


}