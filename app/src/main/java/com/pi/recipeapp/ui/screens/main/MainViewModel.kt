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

class MainViewModel(private val recipeRepository: RecipeRepository) :
    ViewModel() {

    var recipesState by mutableStateOf<UiState<List<Recipe>>>(UiState())
        private set

    var mainViewModelStates: MainViewModelStates by mutableStateOf(MainViewModelStates())
        private set
    private var job: Job? = null
    var currentRecipe: Recipe? by mutableStateOf(null)

    fun onRecipeSearchInputChange(value: String) {
        job?.cancel()
        mainViewModelStates = mainViewModelStates.copy(recipeSearchInput = value)
        if (value.isNotEmpty()) {
            job = viewModelScope.launch {
                delay(2000)
                fetchRecipes()
            }
        }
    }

    fun changeExpandItem() {

        mainViewModelStates = mainViewModelStates.copy(isExpanded = !mainViewModelStates.isExpanded)
    }

    fun fetchRecipe(name: String) {
        currentRecipe = null
        viewModelScope.launch {
            currentRecipe = recipeRepository.fetchRecipeByName(name).data
        }
    }
    private suspend fun fetchRecipes() {
        recipesState = recipesState.copy(
            data = null,
            isLoading = true,
            errorMessage = null
        )
        val result = recipeRepository.fetchMeals(mainViewModelStates.recipeSearchInput)
        delay(1000)
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