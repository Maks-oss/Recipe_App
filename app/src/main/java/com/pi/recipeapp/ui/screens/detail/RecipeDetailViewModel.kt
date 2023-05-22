package com.pi.recipeapp.ui.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.repository.RecipeRepository

class RecipeDetailViewModel(private val recipeRepository: RecipeRepository): ViewModel() {
    private val currentUser = recipeRepository.getCurrentUser()

    var isExpanded: Boolean by mutableStateOf(false)
        private set

    fun addUserRecipeToFavourites(recipe: Recipe) {
        if (currentUser != null) {
            recipeRepository.addRecipeToUserFavorites(recipe)
        }
    }
//
    fun changeExpandItem() {
        isExpanded = !isExpanded
//        textSearchStates = textSearchStates.copy(isExpanded = !textSearchStates.isExpanded)
    }
}