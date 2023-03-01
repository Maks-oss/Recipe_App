package com.pi.recipeapp.retrofit

import com.pi.recipeapp.data.dto.Categories
import com.pi.recipeapp.data.dto.Ingredients
import com.pi.recipeapp.data.dto.RecipeDto
import retrofit2.http.GET
import retrofit2.http.Query


interface RecipesService {
    @GET("search.php")
    suspend fun getRecipesByNamesResponse(@Query("s") query: String): RecipeDto

    @GET("list.php")
    suspend fun getCategoriesResponse(@Query("c") query: String = "list"): Categories

    @GET("list.php")
    suspend fun getIngredientsResponse(@Query("i") query: String = "list"): Ingredients
}