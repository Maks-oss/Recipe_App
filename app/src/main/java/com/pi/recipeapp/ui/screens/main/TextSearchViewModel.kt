package com.pi.recipeapp.ui.screens.main

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
import com.pi.recipeapp.ui.screens.saved.SavedRecipesStates
import com.pi.recipeapp.ui.utils.UiState
import com.pi.recipeapp.utils.Response
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class TextSearchViewModel(private val recipeRepository: RecipeRepository) :
    ViewModel() {

    var recipesTextSearchState by mutableStateOf<UiState<List<Recipe>>>(UiState())
        private set

//    var textSearchStates: TextSearchStates by mutableStateOf(TextSearchStates())
//        private set

    var currentRecipe: Recipe? = null
    val currentUser = recipeRepository.getCurrentUser()
//    get() {
//       val user = recipeRepository.getCurrentUser()
//       recipeRepository.addUserSavedRecipesListener {  }
//    }
//    var currentUser: FirebaseUser? by Delegates.observable(Firebase.auth.currentUser) { _, _, newValue ->
//        if (newValue != null) {
//            recipeRepository.addUserSavedRecipesListener { savedRecipes = it }
//        }
//    }
    var recipeSearchInput: String by mutableStateOf("")
        private set
    private var job: Job? = null


    fun onRecipeSearchInputChange(value: String) {
        job?.cancel()
        recipeSearchInput = value
//        textSearchStates = textSearchStates.copy(recipeSearchInput = value)
        if (value.isNotEmpty()) {
            job = viewModelScope.launch {
                delay(2000)
                fetchTextRecipesSearch()
            }
        }
    }

//    fun addUserRecipeToFavourites(recipe: Recipe) {
//        if (currentUser != null) {
//            recipeRepository.addRecipeToUserFavorites(recipe)
//        }
//    }
//
//    fun removeUserRecipesFromFavorites() {
//        if (currentUser != null) {
//            recipeRepository.removeRecipesFromUserFavorites(
//                savedRecipesStates.selectedRecipes
//            )
//            clearSavedRecipesState()
//        }
//    }

//    fun changeExpandItem() {
//        textSearchStates = textSearchStates.copy(isExpanded = !textSearchStates.isExpanded)
//    }

//    fun selectRecipe(recipe: Recipe) {
//        if (savedRecipes != null) {
//            val selectedRecipes = savedRecipesStates.selectedRecipes.toMutableList().apply {
//                add(recipe)
//            }
//            savedRecipesStates = savedRecipesStates.copy(isDeleteEnabled = true, selectedRecipes)
//        }
//    }
//
//    fun removeSelectedRecipe(recipe: Recipe) {
//        if (savedRecipes != null) {
//            val removedSelectedRecipes = savedRecipesStates.selectedRecipes.toMutableList().apply {
//                remove(recipe)
//            }
//            savedRecipesStates = savedRecipesStates.copy(
//                isDeleteEnabled = removedSelectedRecipes.isNotEmpty(),
//                selectedRecipes = removedSelectedRecipes
//            )
//        }
//    }
//
//    fun clearSavedRecipesState() {
//        savedRecipesStates = savedRecipesStates.copy(false, emptyList())
//    }

    private suspend fun fetchTextRecipesSearch() {
        recipesTextSearchState = recipesTextSearchState.copy(
            data = null,
            isLoading = true,
            errorMessage = null
        )
        val result = recipeRepository.fetchMeals(recipeSearchInput)
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