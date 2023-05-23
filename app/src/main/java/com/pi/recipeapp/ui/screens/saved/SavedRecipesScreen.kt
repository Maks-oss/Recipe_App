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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.FlowRow
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.screens.main.RecipeGridList

@Composable
fun SavedRecipesScreen(
    provideSavedRecipes: () -> List<Recipe?>?,
    provideSavedRecipesState: () -> SavedRecipesStates,
    clearSavedRecipesStates: () -> Unit,
    onRecipeItemClick: (Recipe) -> Unit,
    onRecipeItemLongClick: (Recipe, isSelected: Boolean) -> Unit
) {
    val savedRecipes = provideSavedRecipes()
    val savedRecipesStates = provideSavedRecipesState()
    CleanUpSavedRecipeStates(clearSavedRecipesStates)
    if (savedRecipes != null) {
        SavedRecipeList(
            recipes = savedRecipes,
            onRecipeItemClick = onRecipeItemClick,
            onRecipeItemLongClick = onRecipeItemLongClick,
            selectedRecipes = savedRecipesStates.selectedRecipes
        )
    }
}

@Composable
private fun CleanUpSavedRecipeStates(clearSavedRecipesStates: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                clearSavedRecipesStates()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun SavedRecipeList(
    recipes: List<Recipe?>,
    selectedRecipes: Map<Recipe, Boolean>,
    onRecipeItemClick: (Recipe) -> Unit,
    onRecipeItemLongClick: (Recipe, isSelected: Boolean) -> Unit
) {
    val recipeByCategories = recipes.filterNotNull().groupBy { it.category }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {

        for ((category, recipesList) in recipeByCategories) {
            Column {
                Text(
                    text = category,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(8.dp)
                )
                Divider()
                RecipeGridList(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    recipes = recipesList,
                    onRecipeItemClick = onRecipeItemClick,
                    onRecipeItemLongClick = onRecipeItemLongClick,
                    selectedRecipes = selectedRecipes
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SavedRecipesScreenPreview() {
//    SavedRecipesScreen({}) { emptyList() }
}