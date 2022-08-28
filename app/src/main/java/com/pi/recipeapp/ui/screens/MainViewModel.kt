package com.pi.recipeapp.ui.screens

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.mapper.RecipesMapper
import com.pi.recipeapp.retrofit.RecipesService
import com.pi.recipeapp.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class MainViewModel(private val recipesService: RecipesService = RetrofitClient.recipesService!!): ViewModel() {
    var recipesState by mutableStateOf<List<Recipe>>(emptyList())
        private set

    fun applyRecipes(query:String) = viewModelScope.launch {
        val recipesBody = recipesService.getRecipesByNamesResponse(query).also {
            Log.d("TAG", "applyRecipes: ${it.raw().request.url} ${it.body()}")
        }.body()
        recipesState = RecipesMapper.convertRecipeDtoToDomain(recipesBody!!)
    }

}