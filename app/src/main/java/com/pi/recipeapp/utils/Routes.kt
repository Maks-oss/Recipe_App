package com.pi.recipeapp.utils

sealed class Routes(val route: String) {

    object LoginScreenRoute: Routes("LoginScreen")

    object RegistrationScreenRoute: Routes("RegistrationScreen")
    object MainScreenRoute: Routes("MainScreen")
    object DetailScreenRoute: Routes("DetailScreen")
    object RecipeBuilderScreenRoute: Routes("RecipeBuilderScreen")
    object RecipeImageSearchScreen: Routes("RecipeImageSearchScreen")
}