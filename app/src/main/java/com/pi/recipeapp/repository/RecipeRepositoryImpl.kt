package com.pi.recipeapp.repository

import android.util.Log
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.mapper.RecipesMapper
import com.pi.recipeapp.retrofit.RecipesService
import com.pi.recipeapp.room.RecipesDao
import com.pi.recipeapp.utils.Response

class RecipeRepositoryImpl(
    private val recipesService: RecipesService,
    private val recipesDao: RecipesDao
) : RecipeRepository {
    private val TAG = "RecipeRepository"
    override suspend fun fetchMeals(query: String): Response<List<Recipe>> {
        val recipesFromDb = recipesDao.getAllRecipes(query)
        if (recipesFromDb.isNotEmpty()) {
            Log.d(TAG, "Recipes from db")
            return Response.Success(recipesFromDb.map(RecipesMapper::convertRecipeWithIngredientsToRecipe))
        }
        return try {
            val recipes = recipesService.getRecipesByNamesResponse(query)
            if (recipes.meals.isNullOrEmpty()) {
                Response.Success(emptyList<List<Recipe>>())
            }
            Log.d(TAG, "Recipes from server")

            val recipeList = RecipesMapper.convertRecipeDtoToDomain(recipes)
            insertIntoDatabase(recipeList, query)
            Response.Success(recipeList)
        } catch (exc: Exception) {
            Log.e(TAG,"fetchMeals: ${exc.message}")
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
            Log.e(TAG,"fetchMeals: ${exc.message}")
            Response.Error(exc.message)
        }
    }

    private suspend fun insertIntoDatabase(recipeList: List<Recipe>, query: String) {
        recipeList.forEach { recipe ->
            recipesDao.insertRecipe(RecipesMapper.convertRecipetoRecipeEntity(recipe, query))
            recipesDao.insertIngredients(RecipesMapper.convertRecipeToIngredients(recipe))
        }
    }

//    override suspend fun fetchMealsFromDatabase(query: String): Flow<Recipe> {
//        return recipesDao.getAllRecipes(query).map(RecipesMapper::convertRecipeWithIngredientsToRecipe)
//    }
}