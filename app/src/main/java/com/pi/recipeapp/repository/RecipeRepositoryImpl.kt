package com.pi.recipeapp.repository

import android.util.Log
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.mapper.RecipesMapper
import com.pi.recipeapp.retrofit.RecipesService
import com.pi.recipeapp.room.RecipesDao
import com.pi.recipeapp.room.entity.Category
import com.pi.recipeapp.room.entity.Ingredient
import com.pi.recipeapp.utils.Response

class RecipeRepositoryImpl(
    private val recipesService: RecipesService,
    private val recipesDao: RecipesDao
) : RecipeRepository {
    private val TAG = "RecipeRepository"
    override suspend fun fetchMeals(query: String): Response<List<Recipe>> {
//        val recipesFromDb = recipesDao.getAllRecipes(query)
//        if (recipesFromDb.isNotEmpty()) {
//            Log.d(TAG, "Recipes from db")
//            return Response.Success(recipesFromDb.map(RecipesMapper::convertRecipeWithIngredientsToRecipe))
//        }
        return try {
            val recipes = recipesService.getRecipesByNamesResponse(query)
            if (recipes.meals.isNullOrEmpty()) {
                Response.Success(emptyList<List<Recipe>>())
            }
            Log.d(TAG, "Recipes from server")

            val recipeList = RecipesMapper.convertRecipeDtoToDomain(recipes)
//            insertIntoDatabase(recipeList, query)
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

    override suspend fun fetchCategories(): List<String> {
        val categories = recipesDao.getCategories()
        if (categories.isNotEmpty()) return categories
        return RecipesMapper.convertCategoriesToStringList(recipesService.getCategoriesResponse()).also { categoriesList ->
            insertCategoriesIntoDatabase(categoriesList)
        }
    }

    override suspend fun fetchIngredients(): List<String> {
        val ingredients = recipesDao.getIngredients()
        if (ingredients.isNotEmpty()) return ingredients
        return RecipesMapper.convertIngredientsToStringList(recipesService.getIngredientsResponse()).also { ingredientsList ->
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