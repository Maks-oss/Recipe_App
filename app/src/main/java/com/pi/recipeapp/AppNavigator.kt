package com.pi.recipeapp

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import com.pi.recipeapp.firebase.auth.GoogleAuth
import com.pi.recipeapp.firebase.auth.InAppAuth
import com.pi.recipeapp.firebase.utils.FirebaseUtil
import com.pi.recipeapp.ui.navigation.NavigationExtension

import com.pi.recipeapp.ui.scaffold_components.RecipeModalDrawerContent
import com.pi.recipeapp.ui.scaffold_components.RecipeTopAppBar
import com.pi.recipeapp.ui.screens.DetailScreen
import com.pi.recipeapp.ui.screens.build.BuildRecipeScreen
import com.pi.recipeapp.ui.screens.build.BuildRecipeViewModel
import com.pi.recipeapp.ui.screens.detail.RecipeDetailViewModel
import com.pi.recipeapp.ui.screens.imagesearch.ImageSearchViewModel
import com.pi.recipeapp.ui.screens.imagesearch.RecipeImageSearchScreen
import com.pi.recipeapp.ui.screens.login.LoginScreen
import com.pi.recipeapp.ui.screens.login.RegistrationScreen
import com.pi.recipeapp.ui.screens.main.TextSearchScreen
import com.pi.recipeapp.ui.screens.main.TextSearchViewModel
import com.pi.recipeapp.ui.screens.saved.SavedRecipesScreen
import com.pi.recipeapp.ui.screens.saved.SavedRecipesViewModel
import com.pi.recipeapp.utils.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

import org.koin.androidx.compose.getViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AppNavigator() {
    val googleAuth = get<GoogleAuth>()
    val inAppAuth = get<InAppAuth>()
    val navController = rememberNavController()
    val textSearchViewModel = getViewModel<TextSearchViewModel>()
    val recipeDetailViewModel = getViewModel<RecipeDetailViewModel>()
    val imageSearchViewModel = getViewModel<ImageSearchViewModel>()
    val buildRecipeViewModel = getViewModel<BuildRecipeViewModel>()
    val savedRecipesViewModel = getViewModel<SavedRecipesViewModel>()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val firebaseUtil = get<FirebaseUtil>()
    val navigationExtension =
        NavigationExtension(coroutineScope, scaffoldState, textSearchViewModel, navController)
    val googleSignIn = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            googleAuth.googleAuthorize(
                result,
                onSuccess = { user ->
                    onAuthorizationSuccess(firebaseUtil, user, navigationExtension)
                }, onFailure = { exc ->
                    showSnackbarMessage(
                        coroutineScope,
                        scaffoldState,
                        "Authorization failed ${exc?.message}"
                    )
                })
        })

    val startDestination =
        if (firebaseUtil.currentUser == null) Routes.LoginScreenRoute.route else Routes.RecipeDrawerGraphRoute.route
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LoginScreenRoute.route) {
            Scaffold(scaffoldState = scaffoldState) {
                LoginScreen(navigateToRegistrationScreen = {
                    navController.navigate(Routes.RegistrationScreenRoute.route)
                }, signInGoogle = {
                    googleSignIn.launch(googleAuth.googleSignInClient.signInIntent)
                }, signInInApp = { email, password ->
                    inAppAuth.signIn(email, password, onSuccess = { user ->
                        onAuthorizationSuccess(firebaseUtil, user, navigationExtension)
                    }, onFailure = { exc ->
                        showSnackbarMessage(
                            coroutineScope,
                            scaffoldState,
                            "Authorization failed ${exc?.message}"
                        )
                    })
                })
            }
        }
        composable(Routes.RegistrationScreenRoute.route) {
            Scaffold(scaffoldState = scaffoldState) {
                RegistrationScreen(register = { email, password, imageUri ->
                    inAppAuth.signUp(email, password, imageUri, onSuccess = { user ->
                        firebaseUtil.currentUser = user
                        navigationExtension.navigateWithPopUp(
                            Routes.TextSearchScreenRoute.route,
                            popUpRoute = Routes.LoginScreenRoute.route
                        )
                    }, onFailure = { exc ->
                        showSnackbarMessage(
                            coroutineScope,
                            scaffoldState,
                            "Authorization failed ${exc?.message}"
                        )
                    })
                })
            }
        }
        navigation(Routes.TextSearchScreenRoute.route, Routes.RecipeDrawerGraphRoute.route) {
            composable(Routes.TextSearchScreenRoute.route) {
                CreateScaffold(
                    navigationExtension = navigationExtension,
                    coroutineScope = coroutineScope,
                    scaffoldState = scaffoldState
                ) {
                    TextSearchScreen(
                        provideSearchInput = textSearchViewModel::recipeSearchInput,
                        provideRecipesState = textSearchViewModel::recipesTextSearchState,
                        onSearchInputChange = textSearchViewModel::onRecipeSearchInputChange,
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
                        provideRecipesImageSearchState = imageSearchViewModel::recipesImageSearchState,
                        imageSearchStates = imageSearchViewModel.imageSearchStates,
                        changeImageBitmap = { imageSearchViewModel.changeImageSearchBitmap(it) },
//                        changeRecipeName = { imageSearchViewModel.changeImageSearchRecipeName(it) },
                        navigateToDetailScreen = navigationExtension::navigateToRecipeDetailScreen,
                        loadRecipesByImage = { imageSearchViewModel.fetchImageRecipesSearch(it) },
                        showSearchError = { message ->
                            showSnackbarMessage(coroutineScope, scaffoldState, message)
                        }
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
                        onGeneratedRecipeInputChange = buildRecipeViewModel::onGenerationRecipeInputChange,
                        generatedRecipeInput = buildRecipeViewModel.generateRecipeText,
                        generateRecipe = buildRecipeViewModel::generateRecipe,
                        generatedRecipeState = buildRecipeViewModel.generatedRecipeState,
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
                        saveRecipe = {
                            buildRecipeViewModel.resetBuildRecipeState()
                            buildRecipeViewModel.saveRecipeToDb(it)
                        }
                    )
                }
            }
            composable(Routes.SavedRecipesScreen.route) {
                CreateScaffold(
                    navigationExtension = navigationExtension,
                    coroutineScope = coroutineScope,
                    scaffoldState = scaffoldState,
                    isDeleteIconVisible = savedRecipesViewModel.savedRecipesStates.isDeleteEnabled,
                    onItemDeleteClick = savedRecipesViewModel::removeUserRecipesFromFavorites
                ) {
                    SavedRecipesScreen(
                        provideSavedRecipes = { savedRecipesViewModel.savedRecipes.value },
                        onRecipeItemClick = navigationExtension::navigateToRecipeDetailScreen,
                        onRecipeItemLongClick = { recipe, isSelected ->
                            if (isSelected) {
                                savedRecipesViewModel.selectRecipe(recipe)
                            } else {
                                savedRecipesViewModel.removeSelectedRecipe(recipe)
                            }
                        },
                        clearSavedRecipesStates = savedRecipesViewModel::clearSavedRecipesState,
                        provideSavedRecipesState = savedRecipesViewModel::savedRecipesStates
                    )
                }
            }

            composable(Routes.DetailScreenRoute.route) {
                DetailScreen(
                    textSearchViewModel.currentRecipe,
                    onExpandClick = recipeDetailViewModel::changeExpandItem,
                    provideExpandedValue = recipeDetailViewModel::isExpanded,
                    onFavouritesClick = recipeDetailViewModel::addUserRecipeToFavourites
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
//    buildRecipeViewModel: BuildRecipeViewModel,
//    savedRecipesViewModel: SavedRecipesViewModel,
//    detailViewModel: RecipeDetailViewModel,
    firebaseUtil: FirebaseUtil,
    user: FirebaseUser?,
    navigationExtension: NavigationExtension
) {
    firebaseUtil.currentUser = user
//    detailViewModel.currentUser = user
//    savedRecipesViewModel.currentUser = user
//    buildRecipeViewModel.currentUser = user
//    savedRecipesViewModel.addSavedRecipesListener(user!!.uid, onRecipeDataChangeCallback = { savedRecipesViewModel.savedRecipes = it})
    navigationExtension.navigateWithPopUp(
        Routes.TextSearchScreenRoute.route,
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
    val user = getViewModel<SavedRecipesViewModel>().currentUser

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
                Routes.TextSearchScreenRoute.route
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