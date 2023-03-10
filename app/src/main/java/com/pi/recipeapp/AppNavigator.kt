package com.pi.recipeapp

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pi.recipeapp.firebase.authorization.GoogleAuth
import com.pi.recipeapp.firebase.authorization.InAppAuth
import com.pi.recipeapp.firebase.database.RealtimeDatabaseUtil
import com.pi.recipeapp.ui.navigation.navigateThroughDrawer
import com.pi.recipeapp.ui.navigation.navigateWithPopUp
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
import org.koin.androidx.compose.getViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppNavigator(googleAuth: GoogleAuth) {
    val navController = rememberNavController()
    val mainViewModel = getViewModel<MainViewModel>()
    val buildRecipeViewModel = getViewModel<BuildRecipeViewModel>()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val googleSignIn = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            googleAuth.googleAuthorize(
                result,
                onSuccess = { user ->
                    onAuthorizationSuccess(mainViewModel, user, navController)
                }, onFailure = { exc ->
                    coroutineScope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(" Authorization failed ${exc?.message}")
                    }
                })
        })
    val inAppAuth = InAppAuth()
    if (mainViewModel.currentUser != null) {
        RealtimeDatabaseUtil.addUserRecipesListener(mainViewModel.currentUser!!.uid, mainViewModel::setSavedRecipes)
    }
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
                        onAuthorizationSuccess(mainViewModel, user, navController)
                    }, onFailure = { exc ->
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(" Authorization failed ${exc?.message}")
                        }
                    })
                })
            }
        }
        composable(Routes.RegistrationScreenRoute.route) {
            Scaffold(scaffoldState = scaffoldState) {
                RegistrationScreen(register = { email, password, imageUri ->
                    inAppAuth.signUp(email, password, imageUri, onSuccess = { user ->
                        mainViewModel.currentUser = user
                        navController.navigateWithPopUp(
                            Routes.MainScreenRoute.route,
                            popUpRoute = Routes.LoginScreenRoute.route
                        )
                    }, onFailure = { exc ->
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(" Authorization failed ${exc?.message}")
                        }
                    })
                })
            }
        }
        navigation(Routes.MainScreenRoute.route, Routes.RecipeDrawerGraphRoute.route) {
            composable(Routes.MainScreenRoute.route) {
                CreateScaffold(
                    navController = navController,
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
            composable(Routes.SavedRecipesScreen.route) {
                CreateScaffold(
                    navController = navController,
                    coroutineScope = coroutineScope,
                    scaffoldState = scaffoldState
                ) {
                    SavedRecipesScreen(mainViewModel::savedRecipes)
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

private fun onAuthorizationSuccess(
    mainViewModel: MainViewModel,
    user: FirebaseUser?,
    navController: NavHostController
) {
    mainViewModel.currentUser = user
    navController.navigateWithPopUp(
        Routes.MainScreenRoute.route,
        popUpRoute = Routes.LoginScreenRoute.route
    )
//    RealtimeDatabaseUtil.addUserRecipesListener(user!!.uid, mainViewModel::setSavedRecipes)
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun CreateScaffold(
    navController: NavController,
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState,
    content: @Composable () -> Unit
) {
    val user = getViewModel<MainViewModel>().currentUser
    Scaffold(scaffoldState = scaffoldState, topBar = {
        RecipeTopAppBar(coroutineScope = coroutineScope, scaffoldState = scaffoldState)
    }, drawerContent = {
        RecipeModalDrawerContent(user = user, navigateToTextSearchScreen = {
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
        }, navigateToSavedRecipesScreen = {
            navController.navigateThroughDrawer(
                Routes.SavedRecipesScreen.route,
                coroutineScope,
                scaffoldState
            )
        }, signOut = {
            Firebase.auth.signOut()
            navController.navigateWithPopUp(
                Routes.LoginScreenRoute.route,
                Routes.RecipeDrawerGraphRoute.route
            )
        })
    }) {
        content()
    }
}