package com.pi.recipeapp.ui.navigation

import androidx.compose.material.ScaffoldState
import androidx.navigation.NavController
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.screens.main.TextSearchViewModel
import com.pi.recipeapp.utils.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NavigationExtension(
    private val coroutineScope: CoroutineScope,
    private val scaffoldState: ScaffoldState,
    private val textSearchViewModel: TextSearchViewModel,
    private val navController: NavController
) {
    fun navigateThroughDrawer(
        route: String
    ) {
        navController.navigate(route)
        coroutineScope.launch {
            scaffoldState.drawerState.close()
        }
    }

    fun navigateWithPopUp(route: String, popUpRoute: String) {
        navController.navigate(route) {
            popUpTo(popUpRoute) {
                inclusive = true
            }
        }
    }

    fun navigateToRecipeDetailScreen(recipe: Recipe) {
        textSearchViewModel.currentRecipe = recipe
        navController.navigate(Routes.DetailScreenRoute.route)
    }
}
