package com.pi.recipeapp.retrofit

import com.pi.recipeapp.data.dto.RecipeDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface RecipesService {
    @GET("search.php")
    suspend fun getRecipesByNamesResponse(@Query("s") query: String): Response<RecipeDto>
}