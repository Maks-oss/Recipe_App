package com.pi.recipeapp.ui.screens.saved

data class SavedRecipesStates(
    val isSelected: Boolean = false,
    val selectedRecipeIndices: List<Int> = emptyList()
)