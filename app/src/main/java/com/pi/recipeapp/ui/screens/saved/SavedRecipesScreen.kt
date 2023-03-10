package com.pi.recipeapp.ui.screens.saved

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.screens.main.RecipeList

@Composable
fun SavedRecipesScreen(provideSavedRecipes: () -> List<Recipe>?) {
    val savedRecipes = provideSavedRecipes()
    if (savedRecipes != null) {
        RecipeList(recipes = savedRecipes, onRecipeItemClick = {})
    }
}

@Preview(showSystemUi = true)
@Composable
fun SavedRecipesScreenPreview() {
    SavedRecipesScreen { emptyList() }
}