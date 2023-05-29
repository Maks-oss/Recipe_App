package com.pi.recipeapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pi.recipeapp.BaseViewModelTest
import com.pi.recipeapp.MainCoroutineRule
import com.pi.recipeapp.repository.RecipeRepository
import com.pi.recipeapp.ui.screens.main.TextSearchViewModel
import com.pi.recipeapp.utils.Response
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TextSearchViewModelTest: BaseViewModelTest() {

    private lateinit var textSearchViewModel: TextSearchViewModel

    @Before
    override fun before() {
        super.before()
        textSearchViewModel = TextSearchViewModel(recipeRepository)
    }

    @Test
    fun `when text search response success, data should be not null and isLoading false`() = runTest {
        coEvery { recipeRepository.fetchMealsByText(any()) } returns Response.Success(emptyList())
        textSearchViewModel.onRecipeSearchInputChange("test")
        delay(3050)
        assertFalse(textSearchViewModel.recipesTextSearchState.isLoading)
        assertTrue(textSearchViewModel.recipesTextSearchState.data != null)
        assertTrue(textSearchViewModel.recipesTextSearchState.errorMessage == null)
    }

    @Test
    fun `when text search response error, data should be null and isLoading false`() = runTest {
        coEvery { recipeRepository.fetchMealsByText(any()) } returns Response.Error("error")
        textSearchViewModel.onRecipeSearchInputChange("test")
        delay(3050)
        assertFalse(textSearchViewModel.recipesTextSearchState.isLoading)
        assertTrue(textSearchViewModel.recipesTextSearchState.data == null)
        assertTrue(textSearchViewModel.recipesTextSearchState.errorMessage == "error")
    }
}