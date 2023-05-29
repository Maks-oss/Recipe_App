package com.pi.recipeapp.search

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.pi.recipeapp.MainActivity
import com.pi.recipeapp.R
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.screens.imagesearch.ImageSearchStates
import com.pi.recipeapp.ui.screens.imagesearch.RecipeImageSearchScreen
import com.pi.recipeapp.ui.utils.UiState
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class RecipeImageSearchTest: BaseRecipesSearchTest() {
    private var imageSearchStates by mutableStateOf(ImageSearchStates())

    companion object TestVariables {
        private var context: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val testImage: Bitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.food_background_light
        )
    }


    @Before
    override fun before() {
        imageSearchStates = ImageSearchStates()
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Test
    fun imageSearchShouldDisplayErrorMessage() {
        composeTestRule.setContent {
            val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()
            Scaffold(scaffoldState = scaffoldState) {
                RecipeImageSearchScreen(
                    provideRecipesImageSearchState = { recipeSearchUiState },
                    imageSearchStates = imageSearchStates,
                    changeImageBitmap = {},
                    navigateToDetailScreen = {},
                    loadRecipesByImage = {
                        recipeSearchUiState =
                            recipeSearchUiState.copy(data = null, errorMessage = testErrorMessage)
                    },
                    showSearchError = {
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(it)
                        }
                    }
                )
            }
        }
        imageSearchStates = imageSearchStates.copy(imageBitmap = testImage)
        super.checkForErrorMessage()
    }


    @Test
    fun imageSearchShouldDisplayData() {
        composeTestRule.setContent {
            RecipeImageSearchScreen(
                provideRecipesImageSearchState = { recipeSearchUiState },
                imageSearchStates = imageSearchStates,
                changeImageBitmap = {},
                navigateToDetailScreen = {},
                loadRecipesByImage = {
                    recipeSearchUiState =
                        recipeSearchUiState.copy(data = testRecipesList, errorMessage = null)
                },
                showSearchError = {}
            )
        }
        imageSearchStates = imageSearchStates.copy(recipeName = "test",imageBitmap = testImage)
        super.checkForDisplayedData("Image Search Result")
    }
}