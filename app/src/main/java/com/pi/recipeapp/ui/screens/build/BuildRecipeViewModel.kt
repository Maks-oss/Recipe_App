package com.pi.recipeapp.ui.screens.build

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.screens.main.MainViewModelStates

class BuildRecipeViewModel: ViewModel() {
    var buildRecipeStates: BuildRecipeStates by mutableStateOf(BuildRecipeStates())
        private set

    fun saveRecipeToDb() {
        // TODO Save created recipe to database
    }

    fun resetBuildRecipeState() {
        buildRecipeStates = BuildRecipeStates()
    }

    fun changeRecipe(recipe: Recipe) {
        buildRecipeStates = buildRecipeStates.copy(recipe = recipe)
    }

    fun changeExpanded(isExpanded: Boolean = false) {
        buildRecipeStates = buildRecipeStates.copy(isExpanded = isExpanded)
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