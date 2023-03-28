package com.pi.recipeapp.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.mapper.RecipesMapper
import com.pi.recipeapp.retrofit.RecipesService
import com.pi.recipeapp.room.RecipesDao
import com.pi.recipeapp.room.entity.Category
import com.pi.recipeapp.room.entity.Ingredient
import com.pi.recipeapp.utils.CloudStorageUtil
import com.pi.recipeapp.utils.Response

class RecipeRepositoryImpl(
    private val recipesService: RecipesService,
    private val recipesDao: RecipesDao,
    private val databaseReference: DatabaseReference,
    private val cloudStorageUtil: CloudStorageUtil
) : RecipeRepository {
    private val TAG = "RecipeRepository"
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

    override suspend fun fetchRecipeByName(name: String): Response<Recipe> {
        return try {
            val recipes = recipesService.getRecipesByNamesResponse(name)
            if (recipes.meals.isNullOrEmpty()) {
                Response.Success(emptyList<List<Recipe>>())
            }
            Log.d(TAG, "Recipes from server")

            val recipe = RecipesMapper.convertRecipeDtoToDomain(recipes).first()
            Response.Success(recipe)
        } catch (exc: Exception) {
            Log.e(TAG, "fetchMeals: ${exc.message}")
            Response.Error(exc.message)
        }
    }

    override fun addRecipeToUserFavorites(userId: String, recipe: Recipe) {
        val recipeKey = databaseReference.child("users/${userId}").push()
        cloudStorageUtil.uploadImageToCloud(
            "recipeImages/${recipeKey.key}.jpg", Uri.parse(recipe.imageUrl), onSuccess = {
                recipeKey.setValue(recipe.copy(imageUrl = it.toString()))
            }, onFailure = {
                Log.e(TAG, "addRecipeToUserFavorites: Uploading recipe image failed ${it?.message}")
            })
    }

    override fun removeRecipesFromUserFavorites(userId: String, recipes: List<Recipe>) {
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
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "deleteUserRecipesFailed: ${databaseError.message}")
            }
        })
    }

    override fun addUserSavedRecipesListener(
        userId: String,
        onRecipeDataChangeCallback: (List<Recipe?>?) -> Unit
    ) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recipes = dataSnapshot.getValue<HashMap<String, Recipe?>>()
                onRecipeDataChangeCallback(recipes?.values?.toList())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadRecipes:onCancelled", databaseError.toException())
            }
        }
        databaseReference.child("users/${userId}").addValueEventListener(postListener)
    }

    override suspend fun fetchCategoriesFromLocalDb(): List<String> {
        val categories = recipesDao.getCategories()
        if (categories.isNotEmpty()) return categories
        return RecipesMapper.convertCategoriesToStringList(recipesService.getCategoriesResponse())
            .also { categoriesList ->
                insertCategoriesIntoDatabase(categoriesList)
            }
    }

    override suspend fun fetchIngredientsFromLocalDb(): List<String> {
        val ingredients = recipesDao.getIngredients()
        if (ingredients.isNotEmpty()) return ingredients
        return RecipesMapper.convertIngredientsToStringList(recipesService.getIngredientsResponse())
            .also { ingredientsList ->
                insertIngredientsIntoDatabase(ingredientsList)
            }
    }

    private suspend fun insertIngredientsIntoDatabase(ingredientList: List<String>) {
        recipesDao.insertIngredients(
            ingredientList.map { ingredient ->
                Ingredient(
                    ingredient.hashCode().toString(),
                    ingredient
                )
            }
        )
    }

    private suspend fun insertCategoriesIntoDatabase(categoriesList: List<String>) {
        recipesDao.insertCategories(
            categoriesList.map { category ->
                Category(
                    category.hashCode().toString(),
                    category
                )
            }
        )
    }
}