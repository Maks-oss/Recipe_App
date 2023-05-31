package com.pi.recipeapp.ui.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.repository.RecipeRepository
import kotlin.properties.Delegates

class RecipeDetailViewModel(private val recipeRepository: RecipeRepository): ViewModel() {
    private val currentUser: FirebaseUser?
        get() = recipeRepository.getCurrentUser()

    var isExpanded: Boolean by mutableStateOf(false)
        private set

    fun addUserRecipeToFavourites(recipe: Recipe) {
        val isRecipeExist = recipeRepository.getSavedRecipes()?.contains(recipe) ?: false
        if (currentUser != null && !isRecipeExist) {
            recipeRepository.addRecipeToUserFavorites(currentUser!!.uid, recipe)
        }
    }

    fun changeExpandItem() {
        isExpanded = !isExpanded
    }
}