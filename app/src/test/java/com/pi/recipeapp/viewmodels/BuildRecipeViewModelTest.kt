package com.pi.recipeapp.viewmodels

import com.pi.recipeapp.BaseViewModelTest
import com.pi.recipeapp.repository.RecipeGeneratorRepository
import com.pi.recipeapp.ui.screens.build.BuildRecipeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildRecipeViewModelTest: BaseViewModelTest() {
    private lateinit var buildRecipeViewModel: BuildRecipeViewModel
    private lateinit var generatorRepository: RecipeGeneratorRepository

    override fun before() {
        super.before()
        generatorRepository = mockk()
        buildRecipeViewModel = BuildRecipeViewModel(generatorRepository, recipeRepository)
    }

    @Test
    fun `when generate recipe success, generated recipe should not be null`() = runTest {
        coEvery { generatorRepository.generateRecipe(any()) } returns ""
        coEvery { generatorRepository.generateImage(any()) } returns ""
        buildRecipeViewModel.generateRecipe()
        print(buildRecipeViewModel.generatedRecipeState)
        assertTrue(buildRecipeViewModel.generatedRecipeState.data != null)
        assertTrue(buildRecipeViewModel.generatedRecipeState.errorMessage == null)
    }

    @Test
    fun `when adding ingredient and measure then remove, there should be no ingredients and measures`() = runTest {
        buildRecipeViewModel.addIngredientAndMeasure()
        buildRecipeViewModel.removeIngredientAndMeasure(0)
        assertTrue(buildRecipeViewModel.buildRecipeStates.ingredients.isEmpty())
        assertTrue(buildRecipeViewModel.buildRecipeStates.measures.isEmpty())
    }

    @Test
    fun `when changing ingredient and measure, then corresponding lists should be updated`() = runTest {
        buildRecipeViewModel.addIngredientAndMeasure()
        buildRecipeViewModel.changeIngredient(0, "testIngredient")
        buildRecipeViewModel.changeMeasure(0, "testMeasure")
        assertTrue(buildRecipeViewModel.buildRecipeStates.ingredients.contains("testIngredient"))
        assertTrue(buildRecipeViewModel.buildRecipeStates.measures.contains("testMeasure"))
    }
}