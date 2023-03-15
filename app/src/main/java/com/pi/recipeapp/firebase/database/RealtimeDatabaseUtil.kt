package com.pi.recipeapp.firebase.database


import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.pi.recipeapp.data.domain.Recipe


object RealtimeDatabaseUtil {
    private const val TAG = "RealtimeDatabase"
    lateinit var databaseReference: DatabaseReference

    fun addUserRecipe(userId: String, recipe: Recipe) {
        databaseReference.child("users/${userId}").push().setValue(recipe)
    }

    fun deleteUserRecipes(userId: String, recipes: List<Recipe>, onDeleteSuccess: () -> Unit) {
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
                onDeleteSuccess()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "deleteUserRecipesFailed: ${databaseError.message}")
            }
        })
    }

    fun addUserRecipesListener(userId: String, onRecipeDataChange: (List<Recipe?>?) -> Unit) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recipes = dataSnapshot.getValue<HashMap<String, Recipe?>>()
                onRecipeDataChange(recipes?.values?.toList())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadRecipes:onCancelled", databaseError.toException())
            }
        }
        databaseReference.child("users/${userId}").addValueEventListener(postListener)

    }

//    private fun onRecipesGetValueEvent(userId: String, event: (List<Recipe>?) -> Unit) {
//        val reference = databaseReference.child("users/${userId}")
//        reference.get().addOnSuccessListener {
//            Log.d(TAG, "getUserRecipesSuccess: ${it.value?.javaClass?.simpleName}")
//            val recipes = it.getValue<HashMap<String, Recipe?>>()
//            event(recipes)
//        }.addOnFailureListener {
//            Log.w(TAG, "getUserRecipesFailed: ${it.message}")
//        }
//    }

}