package com.pi.recipeapp.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import com.pi.recipeapp.MainActivity
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.utils.UiState
import org.junit.Before
import org.junit.Rule

abstract class BaseRecipesSearchTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    protected var recipeSearchUiState by mutableStateOf(UiState<List<Recipe>>())
    protected val testRecipesList = listOf(Recipe(name = "test name1"), Recipe(name = "test name2"))
    protected val testErrorMessage = "Test error"

    @Before
    open fun before() {
        recipeSearchUiState = UiState()
    }

    fun checkForErrorMessage() {
        composeTestRule.waitUntil {
            composeTestRule.onNodeWithText(testErrorMessage).assertIsDisplayed()
            return@waitUntil true
        }
    }

    fun checkForDisplayedData(dataTag: String) {
        composeTestRule.waitUntil(2000) {
            composeTestRule.onAllNodesWithTag(dataTag).assertCountEquals(2)
            composeTestRule.onNodeWithText("test name1").assertIsDisplayed()
            composeTestRule.onNodeWithText("test name2").assertIsDisplayed()
            return@waitUntil true
        }
    }
}