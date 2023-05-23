package com.pi.recipeapp.ui.screens.saved

import android.util.Log
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
    var savedRecipes: List<Recipe?>? by mutableStateOf(null)
        private set
    var currentUser by Delegates.observable(Firebase.auth.currentUser) { _, _, newValue ->
        if (newValue != null) {
            recipesRepository.addUserSavedRecipesListener(newValue.uid) { savedRecipes = it }
        }
    }
    init {
        viewModelScope.launch {
            if (currentUser != null) {
                recipesRepository.addUserSavedRecipesListener(currentUser!!.uid) { savedRecipes = it }
            }
        }
    }



    fun removeUserRecipesFromFavorites() {
        if (currentUser != null) {
            recipesRepository.removeRecipesFromUserFavorites(
                savedRecipesStates.selectedRecipes.filter { it.value }.keys.toList()
            )
            clearSavedRecipesState()
        }
    }

    fun selectRecipe(recipe: Recipe) {
        if (savedRecipes != null) {
            val selectedRecipes = savedRecipesStates.selectedRecipes.toMutableMap().apply {
                this[recipe] = true
            }
            savedRecipesStates = savedRecipesStates.copy(isDeleteEnabled = true, selectedRecipes)

        }
    }

    fun removeSelectedRecipe(recipe: Recipe) {
        if (savedRecipes != null) {
            val removedSelectedRecipes = savedRecipesStates.selectedRecipes.toMutableMap().apply {
                this[recipe] = false
            }
            savedRecipesStates = savedRecipesStates.copy(
                isDeleteEnabled = removedSelectedRecipes.isNotEmpty(),
                selectedRecipes = removedSelectedRecipes
            )
        }
    }

    fun clearSavedRecipesState() {
        savedRecipesStates = savedRecipesStates.copy(false, emptyMap())
    }
}