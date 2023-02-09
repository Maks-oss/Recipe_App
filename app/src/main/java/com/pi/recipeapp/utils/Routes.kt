package com.pi.recipeapp.utils

sealed class Routes(val route: String) {
    object MainScreenRoute: Routes("MainScreen")
    object DetailScreenRoute: Routes("DetailScreen")
    object RecipeImageSearchScreen: Routes("RecipeImageSearchScreen")
}