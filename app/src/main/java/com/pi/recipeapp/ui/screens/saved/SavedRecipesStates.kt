package com.pi.recipeapp.ui.screens.saved

import com.pi.recipeapp.data.domain.Recipe

data class SavedRecipesStates(
    val isDeleteEnabled: Boolean = false,
    val selectedRecipes: Map<Recipe, Boolean> = emptyMap()
)