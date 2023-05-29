package com.pi.recipeapp.ui.screens.build

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.mapper.GeneratedRecipesMapper
import com.pi.recipeapp.repository.RecipeGeneratorRepository
import com.pi.recipeapp.repository.RecipeRepository
import com.pi.recipeapp.repository.RecipeRepositoryImpl
import com.pi.recipeapp.ui.utils.UiState
import com.pi.recipeapp.utils.Response
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class BuildRecipeViewModel(
    private val recipeGeneratorRepository: RecipeGeneratorRepository,
    private val recipeRepository: RecipeRepository
) :
    ViewModel() {
    var buildRecipeStates: BuildRecipeStates by mutableStateOf(BuildRecipeStates())
        private set
    var generateRecipeText by mutableStateOf("")
        private set
    var generatedRecipeState by mutableStateOf(UiState<Recipe?>())
        private set
    private val currentUser: FirebaseUser?
        get() = recipeRepository.getCurrentUser()
    fun saveRecipeToDb(recipe: Recipe) {
        recipeRepository.addRecipeToUserFavorites(currentUser!!.uid, recipe)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateRecipe() {
        generatedRecipeState = generatedRecipeState.copy(data = null, isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val preProcessed = generateRecipeText.replace(" ", ",")
            try {
                val recipe =
                    recipeGeneratorRepository.generateRecipe(preProcessed)
                val recipeImage = recipeGeneratorRepository.generateImage(
                    preProcessed + GeneratedRecipesMapper.getRecipeName(recipe)
                )
                generatedRecipeState = generatedRecipeState.copy(
                    GeneratedRecipesMapper.convertGeneratedRecipeToRecipe(
                        preProcessed + recipe,
                        recipeImage
                    ), isLoading = false, errorMessage = null
                )
            } catch (ex: UnknownHostException) {
                generatedRecipeState = generatedRecipeState.copy(
                    null, isLoading = false, "Could not send request to server. Please check your internet connection"
                )
            } catch (e: Exception) {
                generatedRecipeState = generatedRecipeState.copy(
                    null, isLoading = false, e.message
                )
            }
        }
    }

    fun onGenerationRecipeInputChange(value: String) {
        generateRecipeText = value
    }

    fun resetBuildRecipeState() {
        buildRecipeStates = BuildRecipeStates()
    }

    fun changeRecipe(recipe: Recipe) {
        buildRecipeStates = buildRecipeStates.copy(recipe = recipe)
    }

    fun changeExpanded() {
        buildRecipeStates = buildRecipeStates.copy(isExpanded = !buildRecipeStates.isExpanded)
    }

    fun changeImageBitmap(bitmap: Bitmap?) {
        buildRecipeStates = buildRecipeStates.copy(imageBitmap = bitmap)
    }

    fun changeImageUri(uri: String?) {
        buildRecipeStates = buildRecipeStates.copy(imageUri = uri)
    }

    fun changeRecipeName(name: String) {
        buildRecipeStates = buildRecipeStates.copy(recipeName = name)
    }

    fun changeTextInstruction(textInstruction: String) {
        buildRecipeStates = buildRecipeStates.copy(textInstruction = textInstruction)
    }

    fun changeVideoInstruction(videoInstruction: String) {
        buildRecipeStates = buildRecipeStates.copy(videoInstruction = videoInstruction)
    }

    fun addIngredientAndMeasure(value: String = "") {
        val ingredients = buildRecipeStates.ingredients.toMutableList().apply {
            add(value)
        }
        val measures = buildRecipeStates.measures.toMutableList().apply {
            add(value)
        }
        buildRecipeStates = buildRecipeStates.copy(ingredients = ingredients, measures = measures)
    }

    fun removeIngredientAndMeasure(index: Int) {
        val ingredients = buildRecipeStates.ingredients.toMutableList().apply {
            removeAt(index)
        }
        val measures = buildRecipeStates.measures.toMutableList().apply {
            removeAt(index)
        }
        buildRecipeStates = buildRecipeStates.copy(ingredients = ingredients, measures = measures)
    }

    fun changeIngredient(index: Int, value: String) {
        val ingredients = buildRecipeStates.ingredients.toMutableList().apply {
            this[index] = value
        }
        buildRecipeStates = buildRecipeStates.copy(ingredients = ingredients)
    }

    fun changeMeasure(index: Int, value: String) {
        val measures = buildRecipeStates.measures.toMutableList().apply {
            this[index] = value
        }
        buildRecipeStates = buildRecipeStates.copy(measures = measures)
    }
}