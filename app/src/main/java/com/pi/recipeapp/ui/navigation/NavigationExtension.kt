package com.pi.recipeapp.ui.navigation

import androidx.compose.material.ScaffoldState
import androidx.navigation.NavController
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.screens.main.MainViewModel
import com.pi.recipeapp.utils.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NavigationExtension(
    private val coroutineScope: CoroutineScope,
    private val scaffoldState: ScaffoldState,
    private val mainViewModel: MainViewModel,
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
        mainViewModel.currentRecipe = recipe
        navController.navigate(Routes.DetailScreenRoute.route)
    }
}
