package com.pi.recipeapp.firebase.database


import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.pi.recipeapp.data.domain.Recipe


object RealtimeDatabaseUtil {
    private const val TAG = "RealtimeDatabase"
    lateinit var databaseReference: DatabaseReference

    fun addUserRecipe(userId: String, recipe: Recipe) {
        val reference = databaseReference.child("users/${userId}")
        onRecipesGetValueEvent(userId, event = { recipes ->
            if (recipes != null) {
                val newListData = recipes.toMutableList()
                if (!newListData.contains(recipe)) {
                    reference.setValue(newListData.apply { add(recipe) })
                } else {
                    // TODO Display message about existing recipe
                }
            } else {
                reference.setValue(listOf(recipe))
            }
        })
    }

    fun deleteUserRecipes(userId: String, recipeIndices: List<Int>) {
        onRecipesGetValueEvent(userId, event = { recipes ->
            if (!recipes.isNullOrEmpty()) {
                val recipeIndexes = recipes.filterIndexed { index, _ -> recipeIndices.contains(index) }

                recipeIndexes.forEach {
                    databaseReference.child("users/$userId/$it").removeValue()
                }
            }
        })
    }

    fun addUserRecipesListener(userId: String, onRecipeDataChange: (List<Recipe>?) -> Unit) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recipes = dataSnapshot.getValue<List<Recipe>?>()?.filterNotNull()
                onRecipeDataChange(recipes)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        databaseReference.child("users/${userId}").addValueEventListener(postListener)

    }

    private fun onRecipesGetValueEvent(userId: String, event: (List<Recipe>?) -> Unit) {
        val reference = databaseReference.child("users/${userId}")
        reference.get().addOnSuccessListener {
            Log.d(TAG, "getUserRecipesSuccess: ${it.value?.javaClass?.simpleName}")
            val recipes = it.getValue<List<Recipe>>()
            event(recipes)
        }.addOnFailureListener {
            Log.w(TAG, "getUserRecipesFailed: ${it.message}")
        }
    }

}