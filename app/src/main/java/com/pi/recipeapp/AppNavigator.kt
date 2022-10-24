package com.pi.recipeapp

import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pi.recipeapp.ui.screens.DetailScreen
import com.pi.recipeapp.ui.screens.main.MainScreen
import com.pi.recipeapp.ui.screens.main.MainViewModel
import com.pi.recipeapp.utils.Routes
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val mainViewModel = getViewModel<MainViewModel>()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    NavHost(navController = navController, startDestination = Routes.MainScreenRoute.route) {
        composable(Routes.MainScreenRoute.route) {
            Scaffold(scaffoldState = scaffoldState) {
                MainScreen(
                    provideSearchInput = { mainViewModel.recipeSearchInput },
                    provideRecipesState = { mainViewModel.recipesState },
                    onSearchInputChange = mainViewModel::onRecipeSearchInputChange,
                    navigateToDetailScreen = { recipe ->
                        mainViewModel.currentRecipe = recipe
                        navController.navigate(Routes.DetailScreenRoute.route)
                    },
                    showSnackbar = { message ->
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(message)
                        }
                    })
            }
        }
        composable(Routes.DetailScreenRoute.route) {
            DetailScreen(mainViewModel.currentRecipe)
        }
    }
}