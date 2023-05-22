package com.pi.recipeapp.ui.screens.imagesearch

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.repository.RecipeRepository
import com.pi.recipeapp.ui.utils.UiState
import com.pi.recipeapp.utils.Response
import kotlinx.coroutines.delay

class ImageSearchViewModel(private val recipeRepository: RecipeRepository): ViewModel() {
    var recipesImageSearchState by mutableStateOf<UiState<List<Recipe>>>(UiState())
        private set

    var imageSearchStates: ImageSearchStates by mutableStateOf(ImageSearchStates())
        private set

    suspend fun fetchImageRecipesSearch(name: String) {
        recipesImageSearchState = recipesImageSearchState.copy(
            data = null,
            isLoading = true,
            errorMessage = null
        )
        val result =
            recipeRepository.fetchMeals(name)
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


    fun changeImageSearchBitmap(bitmap: Bitmap?) {
        imageSearchStates = imageSearchStates.copy(imageBitmap = bitmap)
    }

    fun changeImageSearchRecipeName(name: String) {
        imageSearchStates = imageSearchStates.copy(recipeName = name)
    }
}