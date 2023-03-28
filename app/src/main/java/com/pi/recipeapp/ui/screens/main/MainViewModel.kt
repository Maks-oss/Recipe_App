package com.pi.recipeapp.ui.screens.main

import android.graphics.Bitmap
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
import com.pi.recipeapp.ui.screens.imagesearch.ImageSearchStates
import com.pi.recipeapp.ui.screens.saved.SavedRecipesStates
import com.pi.recipeapp.ui.utils.UiState
import com.pi.recipeapp.utils.Response
import kotlinx.coroutines.*
import kotlin.properties.Delegates

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

    var filterContentStates: FilterContentStates by mutableStateOf(FilterContentStates())
        private set

    var savedRecipesStates: SavedRecipesStates by mutableStateOf(SavedRecipesStates())
        private set

    var savedRecipes: List<Recipe?>? by mutableStateOf(null)
        private set


    var currentRecipe: Recipe? = null
    var currentUser: FirebaseUser? by Delegates.observable(Firebase.auth.currentUser) { _, _, newValue ->
        if (newValue != null) {
            recipeRepository.addUserSavedRecipesListener(newValue.uid) { savedRecipes = it }
        }
    }

    private var job: Job? = null
    private var ingredientsMap: Map<String, Boolean> = emptyMap()
    private var textRecipeResultList: List<Recipe>? = null

    init {
        viewModelScope.launch {
            val ingredients = recipeRepository.fetchIngredientsFromLocalDb()
            val categories = recipeRepository.fetchCategoriesFromLocalDb()
            ingredientsMap = filterContentStates.ingredientsMap.toMutableMap().apply {
                putAll(ingredients.map { it to false }.take(10))
            }
            val categoriesMap = filterContentStates.categoriesMap.toMutableMap().apply {
                putAll(categories.map { it to false })
            }
            filterContentStates = filterContentStates.copy(
                ingredientsMap = ingredientsMap,
                categoriesMap = categoriesMap
            )
            if (currentUser != null) {
                recipeRepository.addUserSavedRecipesListener(currentUser!!.uid) { savedRecipes = it }
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

    fun addUserRecipeToFavourites(recipe: Recipe) {
        if (currentUser != null) {
            recipeRepository.addRecipeToUserFavorites(currentUser!!.uid, recipe)
        }
    }

    fun removeUserRecipesFromFavorites() {
        if (currentUser != null) {
            recipeRepository.removeRecipesFromUserFavorites(currentUser!!.uid, savedRecipesStates.selectedRecipes)
            clearSavedRecipesState()
        }
    }

    fun applyFilter() {
        viewModelScope.launch {
            recipesTextSearchState = if (isFilterNotSelected()) {
                recipesTextSearchState.copy(data = textRecipeResultList)
            } else {
                recipesTextSearchState.copy(data = filterRecipes())
            }
        }
    }


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

    fun changeExpandItem() {
        mainViewModelStates = mainViewModelStates.copy(isExpanded = !mainViewModelStates.isExpanded)
    }

    fun changeImageSearchBitmap(bitmap: Bitmap?) {
        imageSearchStates = imageSearchStates.copy(imageBitmap = bitmap)
    }

    fun changeImageSearchRecipeName(name: String) {
        imageSearchStates = imageSearchStates.copy(recipeName = name)
    }

    fun changeIngredientsMapValue(key: String) {
        val map = filterContentStates.ingredientsMap.toMutableMap().apply {
            this[key] = !this[key]!!
        }
        filterContentStates = filterContentStates.copy(ingredientsMap = map)
    }

    fun changeCategoriesMapValue(key: String) {
        val map = filterContentStates.categoriesMap.toMutableMap().apply {
            this[key] = !this[key]!!
        }
        filterContentStates = filterContentStates.copy(categoriesMap = map)
    }

    fun changeIngredientName(value: String) {
        val map = if (value.isEmpty()) {
            ingredientsMap
        } else {
            filterContentStates.ingredientsMap.filter { it.key.contains(value, ignoreCase = true) }
        }
        filterContentStates = filterContentStates.copy(ingredientName = value, ingredientsMap = map)
    }

    fun selectRecipe(recipe: Recipe) {
        if (savedRecipes != null) {
            val selectedRecipes = savedRecipesStates.selectedRecipes.toMutableList().apply {
                add(recipe)
            }
            savedRecipesStates = savedRecipesStates.copy(isDeleteEnabled = true, selectedRecipes)
        }
    }

    fun removeSelectedRecipe(recipe: Recipe) {
        if (savedRecipes != null) {
            val removedSelectedRecipes = savedRecipesStates.selectedRecipes.toMutableList().apply {
                remove(recipe)
            }
            savedRecipesStates = savedRecipesStates.copy(
                isDeleteEnabled = removedSelectedRecipes.isNotEmpty(),
                selectedRecipes = removedSelectedRecipes
            )
        }
    }

    fun clearSavedRecipesState() {
        savedRecipesStates = savedRecipesStates.copy(false, emptyList())
    }

    private suspend fun fetchTextRecipesSearch() {
        recipesTextSearchState = recipesTextSearchState.copy(
            data = null,
            isLoading = true,
            errorMessage = null
        )
        val result = recipeRepository.fetchMeals(mainViewModelStates.recipeSearchInput)
        textRecipeResultList = result.data
        delay(1000)
        recipesTextSearchState = when (result) {
            is Response.Success -> recipesTextSearchState.copy(
                data = if (isFilterNotSelected()) result.data else filterRecipes(),
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

    private fun isFilterNotSelected(): Boolean {
        return filterContentStates.categoriesMap.filter { it.value }
            .isEmpty() && filterContentStates.ingredientsMap.filter { it.value }.isEmpty()
    }

    private suspend fun filterRecipes(): List<Recipe> = withContext(Dispatchers.Default) {
        val ingredients = filterContentStates.ingredientsMap.filter { it.value }.keys
        val categories = filterContentStates.categoriesMap.filter { it.value }.keys
        val newList = mutableListOf<Recipe>()
        if (textRecipeResultList != null) {
            for (recipe in textRecipeResultList!!) {
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

}