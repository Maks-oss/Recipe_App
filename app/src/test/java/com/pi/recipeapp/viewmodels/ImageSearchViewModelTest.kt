package com.pi.recipeapp.viewmodels

import com.pi.recipeapp.BaseViewModelTest
import com.pi.recipeapp.ui.screens.imagesearch.ImageSearchViewModel
import com.pi.recipeapp.utils.Response
import io.mockk.coEvery
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ImageSearchViewModelTest: BaseViewModelTest() {

    private lateinit var imageSearchViewModel: ImageSearchViewModel

    override fun before() {
        super.before()
        imageSearchViewModel = ImageSearchViewModel(recipeRepository)
    }

    @Test
    fun `when image search response success, data should be not null and isLoading false`() = runTest {
        coEvery { recipeRepository.fetchMealsByPhoto(any()) } returns ("test" to Response.Success(emptyList()))
        imageSearchViewModel.fetchImageRecipesSearch(null)
        delay(3050)
        Assert.assertFalse(imageSearchViewModel.recipesImageSearchState.isLoading)
        Assert.assertTrue(imageSearchViewModel.recipesImageSearchState.data != null)
        Assert.assertTrue(imageSearchViewModel.recipesImageSearchState.errorMessage == null)
    }

    @Test
    fun `when image search response error, data should be null and isLoading false`() = runTest {
        coEvery { recipeRepository.fetchMealsByPhoto(any()) } returns ("" to Response.Error("error"))
        imageSearchViewModel.fetchImageRecipesSearch(null)
        delay(3050)
        Assert.assertFalse(imageSearchViewModel.recipesImageSearchState.isLoading)
        Assert.assertTrue(imageSearchViewModel.recipesImageSearchState.data == null)
        Assert.assertTrue(imageSearchViewModel.recipesImageSearchState.errorMessage == "error")
    }
}