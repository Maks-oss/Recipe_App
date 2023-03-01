package com.pi.recipeapp

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pi.recipeapp.ui.navigation.navigateThroughDrawer
import com.pi.recipeapp.ui.scaffold_components.RecipeModalDrawerContent
import com.pi.recipeapp.ui.scaffold_components.RecipeTopAppBar
import com.pi.recipeapp.ui.screens.DetailScreen
import com.pi.recipeapp.ui.screens.build.BuildRecipeScreen
import com.pi.recipeapp.ui.screens.build.BuildRecipeViewModel
import com.pi.recipeapp.ui.screens.imagesearch.RecipeImageSearchScreen
import com.pi.recipeapp.ui.screens.main.MainScreen
import com.pi.recipeapp.ui.screens.main.MainViewModel
import com.pi.recipeapp.utils.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val mainViewModel = getViewModel<MainViewModel>()
    val buildRecipeViewModel = getViewModel<BuildRecipeViewModel>()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = Routes.MainScreenRoute.route) {
        composable(Routes.MainScreenRoute.route) {
            CreateScaffold(
                navController = navController,
                coroutineScope = coroutineScope,
                scaffoldState = scaffoldState
            ) {
                MainScreen(
                    provideSearchInput = mainViewModel.mainViewModelStates::recipeSearchInput,
                    ingredients = mainViewModel.ingredients.value,
                    categories = mainViewModel.categories.value,
                    provideRecipesState = mainViewModel::recipesTextSearchState,
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
            DetailScreen(
                mainViewModel.currentRecipe,
                onExpandClick = mainViewModel::changeExpandItem,
                provideExpandedValue = mainViewModel.mainViewModelStates::isExpanded
            )

        }
        composable(Routes.RecipeImageSearchScreen.route) {
            CreateScaffold(
                navController = navController,
                coroutineScope = coroutineScope,
                scaffoldState = scaffoldState
            ) {
                RecipeImageSearchScreen(
                    provideRecipesImageSearchState = mainViewModel::recipesImageSearchState,
                    imageSearchStates = mainViewModel.imageSearchStates,
                    changeImageBitmap = { mainViewModel.changeImageSearchBitmap(it) },
                    changeRecipeName = { mainViewModel.changeImageSearchRecipeName(it) },
                    navigateToDetailScreen = { recipe ->
                        mainViewModel.currentRecipe = recipe
                        navController.navigate(Routes.DetailScreenRoute.route)
                    },
                    loadRecipes = { name ->
                        mainViewModel.fetchImageRecipesSearch(name)
                    })
            }
        }
        composable(Routes.RecipeBuilderScreenRoute.route) {
            CreateScaffold(
                navController = navController,
                coroutineScope = coroutineScope,
                scaffoldState = scaffoldState
            ) {
                BuildRecipeScreen(
                    buildRecipeStates = buildRecipeViewModel.buildRecipeStates,
                    applyRecipe = buildRecipeViewModel::changeRecipe,
                    onExpandValueChange = buildRecipeViewModel::changeExpanded,
                    onRecipeNameTextChange = buildRecipeViewModel::changeRecipeName,
                    onRecipeImageChange = buildRecipeViewModel::changeImageBitmap,
                    onRecipeUriChange = buildRecipeViewModel::changeImageUri,
                    onIngredientsAndMeasuresAdd = buildRecipeViewModel::addIngredientAndMeasure,
                    onIngredientChange = buildRecipeViewModel::changeIngredient,
                    onMeasureChange = buildRecipeViewModel::changeMeasure,
                    onIngredientsAndMeasuresRemove = buildRecipeViewModel::removeIngredientAndMeasure,
                    onTextInstructionChange = buildRecipeViewModel::changeTextInstruction,
                    onVideoInstructionChange = buildRecipeViewModel::changeVideoInstruction,
                    onConfirmClick = buildRecipeViewModel::resetBuildRecipeState
                )
            }

        }
    }
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun CreateScaffold(
    navController: NavController,
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState,
    content: @Composable () -> Unit
) {
    Scaffold(scaffoldState = scaffoldState, topBar = {
        RecipeTopAppBar(coroutineScope = coroutineScope, scaffoldState = scaffoldState)
    }, drawerContent = {
        RecipeModalDrawerContent(navigateToTextSearchScreen = {
            navController.navigateThroughDrawer(
                Routes.MainScreenRoute.route,
                coroutineScope,
                scaffoldState
            )
        }, navigateToImageSearchScreen = {
            navController.navigateThroughDrawer(
                Routes.RecipeImageSearchScreen.route,
                coroutineScope,
                scaffoldState
            )
        }, navigateToCreateRecipeScreen = {
            navController.navigateThroughDrawer(
                Routes.RecipeBuilderScreenRoute.route,
                coroutineScope,
                scaffoldState
            )
        })
    }) {
        content()
    }
}