package com.pi.recipeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.search.RecipeImageSearchTest
import com.pi.recipeapp.ui.screens.DetailScreen
import com.pi.recipeapp.ui.screens.imagesearch.ImageSearchStates
import com.pi.recipeapp.ui.screens.imagesearch.RecipeImageSearchScreen
import com.pi.recipeapp.ui.screens.main.TextSearchScreen
import com.pi.recipeapp.ui.screens.saved.SavedRecipesScreen
import com.pi.recipeapp.ui.screens.saved.SavedRecipesStates
import com.pi.recipeapp.ui.utils.UiState
import com.pi.recipeapp.utils.Routes
import okhttp3.Route

@Composable
fun AppNavigatorTest(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        val testList = listOf(Recipe(name = "test"))
        composable(Routes.TextSearchScreenRoute.route) {
            TextSearchScreen(
                provideSearchInput = { "" },
                provideRecipesState = { UiState(data = testList) },
                onSearchInputChange = {},
                navigateToDetailScreen = { navController.navigate(Routes.DetailScreenRoute.route)},
                showSnackbar = {}
            )
        }
        composable(Routes.DetailScreenRoute.route) {
            DetailScreen(
                recipe = testList.first(),
                provideExpandedValue = { false },
                onExpandClick = {  },
                onFavouritesClick = {}
            )
        }
        composable(Routes.SavedRecipesScreen.route) {
            SavedRecipesScreen(
                provideSavedRecipes = { testList },
                provideSavedRecipesState = { SavedRecipesStates() },
                clearSavedRecipesStates = { },
                onRecipeItemClick = { navController.navigate(Routes.DetailScreenRoute.route) },
                onRecipeItemLongClick = {_,_->}
            )
        }
        composable(Routes.RecipeImageSearchScreen.route) {
            RecipeImageSearchScreen(
                provideRecipesImageSearchState = { UiState(data = testList) },
                imageSearchStates = ImageSearchStates(imageBitmap = RecipeImageSearchTest.testImage, recipeName = "test"),
                changeImageBitmap = {},
                navigateToDetailScreen = { navController.navigate(Routes.DetailScreenRoute.route) },
                loadRecipesByImage = {},
                showSearchError = {}
            )
        }
    }
}