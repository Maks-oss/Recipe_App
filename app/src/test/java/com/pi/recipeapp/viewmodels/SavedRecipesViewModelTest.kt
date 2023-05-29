package com.pi.recipeapp.viewmodels

import com.google.firebase.auth.FirebaseUser
import com.pi.recipeapp.BaseViewModelTest
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.screens.saved.SavedRecipesViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SavedRecipesViewModelTest: BaseViewModelTest() {
    private lateinit var savedRecipesViewModel: SavedRecipesViewModel
    private var testListData = mutableListOf<Recipe>()
    override fun before() {
        super.before()
        every { recipeRepository.getCurrentUser() } returns mockk()
        savedRecipesViewModel = SavedRecipesViewModel(recipeRepository)
        every { savedRecipesViewModel.currentUser?.uid } returns ""
        mockSavedRecipes()
    }

    @Test
    fun `when there are selected recipes, delete function should remove recipes`() = runTest {
        // test selecting specified recipes
        for (i in 0..1) {
            savedRecipesViewModel.selectRecipe(testListData[i])
        }
        savedRecipesViewModel.removeUserRecipesFromFavorites()
        assertTrue(savedRecipesViewModel.savedRecipes.value?.size == 1)
        assertTrue(savedRecipesViewModel.savedRecipesStates.selectedRecipes.isEmpty())
    }

    @Test
    fun `when there are no selected recipes, delete function should not remove recipes`() = runTest {
        for (i in 0..1) {
            savedRecipesViewModel.selectRecipe(testListData[i])
        }
        // test removing selected recipes
        for (i in 0..1) {
            savedRecipesViewModel.removeSelectedRecipe(testListData[i])
        }
        savedRecipesViewModel.removeUserRecipesFromFavorites()
        assertTrue(savedRecipesViewModel.savedRecipes.value?.size == 3)
        assertTrue(savedRecipesViewModel.savedRecipesStates.selectedRecipes.isEmpty())
    }

    private fun mockSavedRecipes() {
        testListData = mutableListOf(
            Recipe(name = "test name1"),
            Recipe(name = "test name2"),
            Recipe(name = "test name3")
        )
        coEvery { recipeRepository.removeRecipesFromUserFavorites(any(), any()) } coAnswers {
            val selectedRecipes =
                savedRecipesViewModel.savedRecipesStates.selectedRecipes.filter { it.value }.keys.toList()
            selectedRecipes.forEach {
                testListData.remove(it)
            }
        }
        coEvery { recipeRepository.getSavedRecipes() } returns testListData
    }
}