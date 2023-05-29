package com.pi.recipeapp.search

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.pi.recipeapp.MainActivity
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.screens.main.TextSearchScreen
import com.pi.recipeapp.ui.utils.UiState
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecipeTextSearchTest: BaseRecipesSearchTest() {

    private var recipeSearchInput by mutableStateOf("")

    @Before
    override fun before() {
        recipeSearchInput = ""
    }


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Test
    fun recipeSearchShouldDisplayErrorMessage() {
        composeTestRule.setContent {
            val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()
            Scaffold(scaffoldState = scaffoldState) {
                TextSearchScreen(
                    provideSearchInput = { recipeSearchInput },
                    provideRecipesState = { recipeSearchUiState },
                    onSearchInputChange = {
                        recipeSearchInput = it;
                        recipeSearchUiState = recipeSearchUiState.copy(errorMessage = testErrorMessage)
                    },
                    navigateToDetailScreen = {},
                    showSnackbar = {
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(it)
                        }
                    }
                )
            }
        }
        composeTestRule.onNodeWithTag("TextSearchField").performTextInput("test")
        super.checkForErrorMessage()
    }

    @Test
    fun recipeSearchShouldDisplayData() {
        composeTestRule.setContent {
            TextSearchScreen(
                provideSearchInput = { recipeSearchInput },
                provideRecipesState = { recipeSearchUiState },
                onSearchInputChange = {
                    recipeSearchInput = it;
                    recipeSearchUiState = recipeSearchUiState.copy(data = testRecipesList)
                },
                navigateToDetailScreen = {},
                showSnackbar = {}
            )

        }
        composeTestRule.onNodeWithTag("TextSearchField").performTextInput("test")
        super.checkForDisplayedData("Recipe Card")
    }
}