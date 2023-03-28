package com.pi.recipeapp

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pi.recipeapp.auth.GoogleAuth
import com.pi.recipeapp.auth.InAppAuth
import com.pi.recipeapp.ui.navigation.NavigationExtension

import com.pi.recipeapp.ui.scaffold_components.RecipeModalDrawerContent
import com.pi.recipeapp.ui.scaffold_components.RecipeTopAppBar
import com.pi.recipeapp.ui.screens.DetailScreen
import com.pi.recipeapp.ui.screens.build.BuildRecipeScreen
import com.pi.recipeapp.ui.screens.build.BuildRecipeViewModel
import com.pi.recipeapp.ui.screens.imagesearch.RecipeImageSearchScreen
import com.pi.recipeapp.ui.screens.login.LoginScreen
import com.pi.recipeapp.ui.screens.login.RegistrationScreen
import com.pi.recipeapp.ui.screens.main.MainScreen
import com.pi.recipeapp.ui.screens.main.MainViewModel
import com.pi.recipeapp.ui.screens.saved.SavedRecipesScreen
import com.pi.recipeapp.utils.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppNavigator() {
    val googleAuth = get<GoogleAuth>()
    val inAppAuth = get<InAppAuth>()
    val navController = rememberNavController()
    val mainViewModel = getViewModel<MainViewModel>()
    val buildRecipeViewModel = getViewModel<BuildRecipeViewModel>()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val navigationExtension =
        NavigationExtension(coroutineScope, scaffoldState, mainViewModel, navController)
    val googleSignIn = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            googleAuth.googleAuthorize(
                result,
                onSuccess = { user ->
                    onAuthorizationSuccess(mainViewModel, user, navigationExtension)
                }, onFailure = { exc ->
                    showSnackbarMessage(coroutineScope, scaffoldState, "Authorization failed ${exc?.message}")
                })
        })

    val startDestination =
        if (mainViewModel.currentUser == null) Routes.LoginScreenRoute.route else Routes.RecipeDrawerGraphRoute.route
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LoginScreenRoute.route) {
            Scaffold(scaffoldState = scaffoldState) {
                LoginScreen(navigateToRegistrationScreen = {
                    navController.navigate(Routes.RegistrationScreenRoute.route)
                }, signInGoogle = {
                    googleSignIn.launch(googleAuth.googleSignInClient.signInIntent)
                }, signInInApp = { email, password ->
                    inAppAuth.signIn(email, password, onSuccess = { user ->
                        onAuthorizationSuccess(mainViewModel, user, navigationExtension)
                    }, onFailure = { exc ->
                        showSnackbarMessage(coroutineScope, scaffoldState, "Authorization failed ${exc?.message}")
                    })
                })
            }
        }
        composable(Routes.RegistrationScreenRoute.route) {
            Scaffold(scaffoldState = scaffoldState) {
                RegistrationScreen(register = { email, password, imageUri ->
                    inAppAuth.signUp(email, password, imageUri, onSuccess = { user ->
                        mainViewModel.currentUser = user
                        navigationExtension.navigateWithPopUp(
                            Routes.MainScreenRoute.route,
                            popUpRoute = Routes.LoginScreenRoute.route
                        )
                    }, onFailure = { exc ->
                        showSnackbarMessage(coroutineScope, scaffoldState, "Authorization failed ${exc?.message}")
                    })
                })
            }
        }
        navigation(Routes.MainScreenRoute.route, Routes.RecipeDrawerGraphRoute.route) {
            composable(Routes.MainScreenRoute.route) {
                CreateScaffold(
                    navigationExtension = navigationExtension,
                    coroutineScope = coroutineScope,
                    scaffoldState = scaffoldState
                ) {
                    MainScreen(
                        provideSearchInput = mainViewModel.mainViewModelStates::recipeSearchInput,
                        provideFilterContentStates = mainViewModel::filterContentStates,
                        provideRecipesState = mainViewModel::recipesTextSearchState,
                        onSearchInputChange = mainViewModel::onRecipeSearchInputChange,
                        onFilterIngredientNameChangeValue = mainViewModel::changeIngredientName,
                        onFilterCategoriesMapChangeValue = mainViewModel::changeCategoriesMapValue,
                        onFilterIngredientsMapChangeValue = mainViewModel::changeIngredientsMapValue,
                        onApplyFilterClick = mainViewModel::applyFilter,
                        navigateToDetailScreen = navigationExtension::navigateToRecipeDetailScreen,
                        showSnackbar = { message ->
                            showSnackbarMessage(coroutineScope, scaffoldState, message)
                        })
                }
            }
            composable(Routes.RecipeImageSearchScreen.route) {
                CreateScaffold(
                    navigationExtension = navigationExtension,
                    coroutineScope = coroutineScope,
                    scaffoldState = scaffoldState
                ) {
                    RecipeImageSearchScreen(
                        provideRecipesImageSearchState = mainViewModel::recipesImageSearchState,
                        imageSearchStates = mainViewModel.imageSearchStates,
                        changeImageBitmap = { mainViewModel.changeImageSearchBitmap(it) },
                        changeRecipeName = { mainViewModel.changeImageSearchRecipeName(it) },
                        navigateToDetailScreen = navigationExtension::navigateToRecipeDetailScreen,
                        loadRecipes = mainViewModel::fetchImageRecipesSearch
                    )
                }
            }
            composable(Routes.RecipeBuilderScreenRoute.route) {
                CreateScaffold(
                    navigationExtension = navigationExtension,
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
                        onConfirmClick = {
                            buildRecipeViewModel.resetBuildRecipeState()
                            mainViewModel.addUserRecipeToFavourites(it)
                            showSnackbarMessage(coroutineScope, scaffoldState, "Recipe was successfully added!")
                        }
                    )
                }
            }
            composable(Routes.SavedRecipesScreen.route) {
                CreateScaffold(
                    navigationExtension = navigationExtension,
                    coroutineScope = coroutineScope,
                    scaffoldState = scaffoldState,
                    isDeleteIconVisible = mainViewModel.savedRecipesStates.isDeleteEnabled,
                    onItemDeleteClick = mainViewModel::removeUserRecipesFromFavorites
                ) {
                    SavedRecipesScreen(
                        provideSavedRecipes = mainViewModel::savedRecipes,
                        onRecipeItemClick = navigationExtension::navigateToRecipeDetailScreen,
                        onRecipeItemLongClick = { recipe, isSelected ->
                            if (isSelected) {
                                mainViewModel.selectRecipe(recipe)
                            } else {
                                mainViewModel.removeSelectedRecipe(recipe)
                            }
                        }, clearSavedRecipesStates = mainViewModel::clearSavedRecipesState)
                }
            }

            composable(Routes.DetailScreenRoute.route) {
                DetailScreen(
                    mainViewModel.currentRecipe,
                    onExpandClick = mainViewModel::changeExpandItem,
                    provideExpandedValue = mainViewModel.mainViewModelStates::isExpanded,
                    onFavouritesClick = mainViewModel::addUserRecipeToFavourites
                )

            }
        }
    }
}

private fun showSnackbarMessage(
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState,
    message: String
) {
    coroutineScope.launch {
//        scaffoldState.snackbarHostState.showSnackbar(" Authorization failed ${exc?.message}")
        scaffoldState.snackbarHostState.showSnackbar(message)
    }
}

private fun onAuthorizationSuccess(
    mainViewModel: MainViewModel,
    user: FirebaseUser?,
    navigationExtension: NavigationExtension
) {
    mainViewModel.currentUser = user
    navigationExtension.navigateWithPopUp(
        Routes.MainScreenRoute.route,
        popUpRoute = Routes.LoginScreenRoute.route
    )
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun CreateScaffold(
    navigationExtension: NavigationExtension,
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState,
    isDeleteIconVisible: Boolean = false,
    onItemDeleteClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val user = getViewModel<MainViewModel>().currentUser

    Scaffold(scaffoldState = scaffoldState, topBar = {
        RecipeTopAppBar(
            coroutineScope = coroutineScope,
            scaffoldState = scaffoldState,
            isItemsSelected = isDeleteIconVisible,
            onItemDeleteClick = onItemDeleteClick
        )
    }, drawerContent = {
        RecipeModalDrawerContent(user = user, navigateToTextSearchScreen = {
            navigationExtension.navigateThroughDrawer(
                Routes.MainScreenRoute.route
            )
        }, navigateToImageSearchScreen = {
            navigationExtension.navigateThroughDrawer(
                Routes.RecipeImageSearchScreen.route
            )
        }, navigateToCreateRecipeScreen = {
            navigationExtension.navigateThroughDrawer(
                Routes.RecipeBuilderScreenRoute.route
            )
        }, navigateToSavedRecipesScreen = {
            navigationExtension.navigateThroughDrawer(
                Routes.SavedRecipesScreen.route
            )
        }, signOut = {
            Firebase.auth.signOut()
            navigationExtension.navigateWithPopUp(
                Routes.LoginScreenRoute.route,
                Routes.RecipeDrawerGraphRoute.route
            )
        })
    }) {
        content()
    }
}