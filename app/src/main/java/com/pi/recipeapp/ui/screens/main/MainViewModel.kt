package com.pi.recipeapp.ui.screens.main

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.repository.RecipeRepository
import com.pi.recipeapp.ui.screens.imagesearch.ImageSearchStates
import com.pi.recipeapp.ui.screens.uistate.UiState
import com.pi.recipeapp.utils.Response
import kotlinx.coroutines.*

class MainViewModel(private val recipeRepository: RecipeRepository) :
    ViewModel() {

    var recipesTextSearchState by mutableStateOf<UiState<List<Recipe>>>(UiState())
        private set

    var recipesImageSearchState by mutableStateOf<UiState<List<Recipe>>>(UiState())
        private set

    var mainViewModelStates: MainViewModelStates by mutableStateOf(MainViewModelStates())
        private set

    var imageSearchStates: ImageSearchStates by mutableStateOf(ImageSearchStates())
        private set

    private var job: Job? = null
    var currentRecipe : Recipe? = null


    val categories: MutableState<List<String>> by lazy {
        mutableStateOf<List<String>>(emptyList()).apply {
            viewModelScope.launch {
                value = recipeRepository.fetchCategories()
            }
        }
    }

    val ingredients: MutableState<List<String>> by lazy {
        mutableStateOf<List<String>>(emptyList()).apply {
            viewModelScope.launch {
                value = recipeRepository.fetchIngredients()
            }
        }
    }

    fun onRecipeSearchInputChange(value: String) {
        job?.cancel()
        mainViewModelStates = mainViewModelStates.copy(recipeSearchInput = value)
        if (value.isNotEmpty()) {
            job = viewModelScope.launch {
                delay(2000)
                fetchTextRecipesSearch()
            }
        }
    }

    fun applyFilter(ingredients: List<String>, categories: List<String>) {
        viewModelScope.launch {
            if (ingredients.isEmpty() && categories.isEmpty()) {
                fetchTextRecipesSearch()
            } else {
                recipesTextSearchState =
                    recipesTextSearchState.copy(data = filterRecipes(ingredients, categories))
            }
        }
    }
    // chicken, tomato, potato
    private suspend fun filterRecipes(ingredients: List<String>, categories: List<String>): List<Recipe> = withContext(Dispatchers.Default) {
        val list = recipesTextSearchState.data
        val newList = mutableListOf<Recipe>()
        if (list != null) {
            for (recipe in list ) {
                var isIngredientAbsent = false
                for (ingredient in ingredients) {
                    if (ingredient !in recipe.ingredients.keys) {
                        isIngredientAbsent = true
                        break
                    }
                }
                if (isIngredientAbsent) {
                    continue
                }
                if (categories.isNotEmpty() && recipe.category in categories) {
                    newList.add(recipe)
                } else if (categories.isEmpty()) {
                    newList.add(recipe)
                }
            }
        }
        return@withContext newList
    }

    suspend fun fetchImageRecipesSearch(name: String) {
        recipesImageSearchState = recipesImageSearchState.copy(
            data = null,
            isLoading = true,
            errorMessage = null
        )
        val result = recipeRepository.fetchMeals(name)
        delay(1000)
        recipesImageSearchState = when (result) {
            is Response.Success -> recipesImageSearchState.copy(
                data = result.data,
                isLoading = false,
                errorMessage = null
            )
            is Response.Error -> recipesImageSearchState.copy(
                data = null,
                isLoading = false,
                errorMessage = result.errorMessage
            )
        }
    }

    fun changeExpandItem() {
        mainViewModelStates = mainViewModelStates.copy(isExpanded = !mainViewModelStates.isExpanded)
    }

    fun changeImageSearchBitmap(bitmap: Bitmap?) {
        imageSearchStates = imageSearchStates.copy(imageBitmap = bitmap)
    }
    fun changeImageSearchRecipeName(name: String) {
        imageSearchStates = imageSearchStates.copy(recipeName = name)
    }

    fun resetRecipeState() {
        imageSearchStates = imageSearchStates.copy(recipeName = "", imageBitmap = null)
    }

    private suspend fun fetchTextRecipesSearch() {
        recipesTextSearchState = recipesTextSearchState.copy(
            data = null,
            isLoading = true,
            errorMessage = null
        )
        val result = recipeRepository.fetchMeals( mainViewModelStates.recipeSearchInput)
        delay(1000)
        recipesTextSearchState = when (result) {
            is Response.Success -> recipesTextSearchState.copy(
                data = result.data,
                isLoading = false,
                errorMessage = null
            )
            is Response.Error -> recipesTextSearchState.copy(
                data = null,
                isLoading = false,
                errorMessage = result.errorMessage
            )
        }
    }


}