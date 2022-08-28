package com.pi.recipeapp.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://www.themealdb.com/api/json/v1/1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //    private var recipesService: RecipesService? = null
//    fun createRecipesService(): RecipesService {
//        if (recipesService == null) {
//            retrofit.create(RecipesService::class.java)
//        }
//        return recipesService!!
//    }
    var recipesService: RecipesService? = null
        get() {
            return field ?: retrofit.create(RecipesService::class.java)
        }
}