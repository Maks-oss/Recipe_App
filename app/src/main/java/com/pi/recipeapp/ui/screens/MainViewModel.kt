package com.pi.recipeapp.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.mapper.RecipesMapper
import com.pi.recipeapp.retrofit.RecipesService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val recipesService: RecipesService) :
    ViewModel() {
    var recipesState by mutableStateOf<List<Recipe>>(emptyList())
        private set
    var isRecipesLoading by mutableStateOf(false)
        private set

    var recipeSearchInput by mutableStateOf("")
        private set
    private var job: Job? = null

    fun onRecipeSearchInputChange(value: String) {
        recipeSearchInput = value
        if (recipeSearchInput.isNotEmpty()) {
            isRecipesLoading = true
            job?.cancel()
            job = viewModelScope.launch {
                delay(3000)
                applyRecipes(recipeSearchInput)
            }
        }
    }

    private suspend fun applyRecipes(query: String) {
        val recipesBody = recipesService.getRecipesByNamesResponse(query).also {
            Log.d("TAG", "applyRecipes: ${it.raw().request.url} ${it.body()}")
        }.body()
        recipesState = RecipesMapper.convertRecipeDtoToDomain(recipesBody!!)
        isRecipesLoading = false
    }

}