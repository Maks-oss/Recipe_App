package com.pi.recipeapp.ui.screens.saved

import android.util.Log
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.FlowRow
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.screens.main.RecipeGridList

@Composable
fun SavedRecipesScreen(
    provideSavedRecipes: () -> List<Recipe>?,
    onRecipeItemClick: (Recipe) -> Unit
) {
    val savedRecipes = provideSavedRecipes()
    if (savedRecipes != null) {
        SavedRecipeList(recipes = savedRecipes, onRecipeItemClick = onRecipeItemClick)
    }
}

@Composable
fun SavedRecipeList(recipes: List<Recipe>, onRecipeItemClick: (Recipe) -> Unit) {
    val recipeByCategories = recipes.groupBy { it.category }
    Log.d(
        "TAG",
        "RecipeList: ${recipeByCategories.entries.map { listEntry -> listEntry.key to listEntry.value.map { it.name } }}"
    )
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        for ((category, recipesList) in recipeByCategories) {
            Column {
                Text(text = category, style = MaterialTheme.typography.h5, modifier = Modifier.padding(8.dp))
                Divider()
                RecipeGridList(modifier = Modifier.fillMaxWidth().height(300.dp),recipes = recipesList, onRecipeItemClick = onRecipeItemClick)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SavedRecipesScreenPreview() {
//    SavedRecipesScreen({}) { emptyList() }
}