package com.pi.recipeapp.ui.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.repository.RecipeRepository
import com.pi.recipeapp.ui.screens.uistate.UiState
import com.pi.recipeapp.utils.Response
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MainViewModel(private val recipeRepository: RecipeRepository) :
    ViewModel() {

    var recipesState by mutableStateOf<UiState<List<Recipe>>>(UiState())
        private set
    var recipeSearchInput: String by mutableStateOf("")
        private set
    private var job: Job? = null
    var currentRecipe: Recipe by Delegates.notNull()

    fun onRecipeSearchInputChange(value: String) {
        job?.cancel()
        if (value.isNotEmpty()) {
            job = viewModelScope.launch {
                recipeSearchInput = value
                delay(2000)
                getRecipes()
            }
        }
    }

    private suspend fun getRecipes() {
        recipesState = recipesState.copy(
            data = null,
            isLoading = true,
            errorMessage = null
        )
        delay(1000)
        val result = recipeRepository.fetchMeals(recipeSearchInput)
        recipesState = when (result) {
            is Response.Success -> recipesState.copy(
                data = result.data,
                isLoading = false,
                errorMessage = null
            )
            is Response.Error -> recipesState.copy(
                data = null,
                isLoading = false,
                errorMessage = result.errorMessage
            )
        }
    }

}