package com.pi.recipeapp.ui.screens.saved

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.repository.RecipeRepository
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class SavedRecipesViewModel(private val recipesRepository: RecipeRepository) : ViewModel() {
    var savedRecipesStates: SavedRecipesStates by mutableStateOf(SavedRecipesStates())
        private set
    val savedRecipes: MutableState<List<Recipe?>?>
        get() {
            return mutableStateOf(recipesRepository.getSavedRecipes())
        }
    val currentUser = recipesRepository.getCurrentUser()

    fun removeUserRecipesFromFavorites() {
        if (currentUser != null) {
            recipesRepository.removeRecipesFromUserFavorites(
                currentUser.uid,
                savedRecipesStates.selectedRecipes.filter { it.value }.keys.toList()
            )
            clearSavedRecipesState()
        }
    }

    fun selectRecipe(recipe: Recipe) {
        val selectedRecipes = savedRecipesStates.selectedRecipes.toMutableMap().apply {
            this[recipe] = true
        }
        savedRecipesStates = savedRecipesStates.copy(isDeleteEnabled = true, selectedRecipes)
    }

    fun removeSelectedRecipe(recipe: Recipe) {
        val removedSelectedRecipes = savedRecipesStates.selectedRecipes.toMutableMap().apply {
            this[recipe] = false
        }
        savedRecipesStates = savedRecipesStates.copy(
            isDeleteEnabled = removedSelectedRecipes.filter { it.value }.isNotEmpty(),
            selectedRecipes = removedSelectedRecipes
        )
    }

    fun clearSavedRecipesState() {
        savedRecipesStates = savedRecipesStates.copy(false, emptyMap())
    }
}