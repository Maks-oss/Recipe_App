package com.pi.recipeapp.utils

sealed class Routes(val route: String) {
    object LoginScreenRoute : Routes("LoginScreen")
    object RegistrationScreenRoute : Routes("RegistrationScreen")
    object RecipeDrawerGraphRoute : Routes("RecipeDrawerGraph")
    object RecipeBuilderScreenRoute : Routes("RecipeBuilderScreen")
    object RecipeImageSearchScreen : Routes("RecipeImageSearchScreen")
    object SavedRecipesScreen: Routes("SavedRecipesScreen")
    object MainScreenRoute : Routes("MainScreen")
    object DetailScreenRoute : Routes("DetailScreen")
}