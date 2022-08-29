package com.pi.recipeapp.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.recipeapp.R
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.mapper.RecipesMapper
import com.pi.recipeapp.retrofit.RecipesService
import com.pi.recipeapp.utils.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val recipesService: RecipesService) :
    ViewModel() {

    var recipesState by mutableStateOf<Result<List<Recipe>>>(Result.default())
        private set
    var recipeSearchInput by mutableStateOf("")
        private set
    private var job: Job? = null

    fun onRecipeSearchInputChange(value: String) {
        recipeSearchInput = value
        job?.cancel()
        job = viewModelScope.launch {
            recipesState = Result.loading()
            delay(2000)
            applyRecipes()
        }

    }

    private suspend fun applyRecipes() {
        if (recipeSearchInput.isNotEmpty()) {
            val recipesBody = recipesService.getRecipesByNamesResponse(recipeSearchInput).also {
                Log.d("TAG", "applyRecipes: ${it.raw().request.url} ${it.body()}")
            }.body()
            val recipesDomain = RecipesMapper.convertRecipeDtoToDomain(recipesBody!!)
            recipesState = Result.success(recipesDomain)
            if (recipesState.data.isNullOrEmpty()) {
                recipesState = Result.error(R.string.empty_request_error)
            }
        }
    }

}