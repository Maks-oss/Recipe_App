package com.pi.recipeapp.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.pi.recipeapp.MainActivity
import com.pi.recipeapp.utils.Routes
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationToDetailScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    lateinit var testNavHostController : NavHostController

    @Test
    fun navigationFromImageSearchToDetail() {
        setupAppNavigator(startDestination = Routes.RecipeImageSearchScreen.route)
        composeTestRule.onNodeWithTag("Recipe Button").performClick()
        checkDestinationRoute()
    }

    @Test
    fun navigationFromTextSearchToDetail() {
        setupAppNavigator(startDestination = Routes.TextSearchScreenRoute.route)
        composeTestRule.onNodeWithText("test").performClick()
        checkDestinationRoute()
    }

    @Test
    fun navigationFromSavedToDetail() {
        setupAppNavigator(startDestination = Routes.SavedRecipesScreen.route)
        composeTestRule.onNodeWithText("test").performClick()
        checkDestinationRoute()
    }

    private fun checkDestinationRoute() {
        val route = testNavHostController.currentDestination?.route
        Assert.assertEquals(Routes.DetailScreenRoute.route, route)
    }

    private fun setupAppNavigator(startDestination: String) {
        composeTestRule.setContent {
            testNavHostController = rememberNavController()
            AppNavigatorTest(navController = testNavHostController, startDestination)
        }
    }
}