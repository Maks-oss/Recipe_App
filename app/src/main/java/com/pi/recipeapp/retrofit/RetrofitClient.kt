package com.pi.recipeapp.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    fun provideRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl("http://www.themealdb.com/api/json/v1/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun provideRecipesService(retrofit: Retrofit):RecipesService{
        return retrofit.create(RecipesService::class.java)
    }
}