package com.pi.recipeapp.ui.screens.imagesearch

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.repository.RecipeRepository
import com.pi.recipeapp.ui.utils.UiState
import com.pi.recipeapp.utils.Response
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImageSearchViewModel(private val recipeRepository: RecipeRepository): ViewModel() {
    var recipesImageSearchState by mutableStateOf<UiState<List<Recipe>>>(UiState())
        private set

    var imageSearchStates: ImageSearchStates by mutableStateOf(ImageSearchStates())
        private set

    fun fetchImageRecipesSearch(image: Bitmap?) {
        viewModelScope.launch {
            recipesImageSearchState = recipesImageSearchState.copy(
                data = null,
                isLoading = true,
                errorMessage = null
            )
            val result =
                recipeRepository.fetchMealsByPhoto(image)
            delay(1000)

            when (result.second) {
                is Response.Success -> {
                    recipesImageSearchState = recipesImageSearchState.copy(
                        data = result.second.data,
                        isLoading = false,
                        errorMessage = null
                    )
                    imageSearchStates = imageSearchStates.copy(recipeName = result.first)
                }
                is Response.Error -> {
                    recipesImageSearchState = recipesImageSearchState.copy(
                        data = null,
                        isLoading = false,
                        errorMessage = result.second.errorMessage
                    )
                    imageSearchStates = imageSearchStates.copy(recipeName = result.first)
                }
            }
        }
    }


    fun changeImageSearchBitmap(bitmap: Bitmap?) {
        imageSearchStates = imageSearchStates.copy(imageBitmap = bitmap)
    }

//    fun changeImageSearchRecipeName(name: String) {
//        imageSearchStates = imageSearchStates.copy(recipeName = name)
//    }
}