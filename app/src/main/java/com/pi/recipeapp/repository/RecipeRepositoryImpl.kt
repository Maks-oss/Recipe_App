package com.pi.recipeapp.repository

import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.mapper.RecipesMapper
import com.pi.recipeapp.retrofit.RecipesService
import com.pi.recipeapp.utils.Response

class RecipeRepositoryImpl(private val recipesService: RecipesService) : RecipeRepository {
    override suspend fun fetchMeals(query: String): Response<List<Recipe>> {
        return try {
            val recipes = recipesService.getRecipesByNamesResponse(query)
            if (recipes.meals.isNullOrEmpty()){
                Response.Success(emptyList<List<Recipe>>())
            }
            Response.Success(RecipesMapper.convertRecipeDtoToDomain(recipes))
        } catch (exc: Exception){
            Response.Error(exc.message)
        }

    }
}