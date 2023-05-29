package com.pi.recipeapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pi.recipeapp.repository.RecipeGeneratorRepository
import com.pi.recipeapp.repository.RecipeRepository
import com.pi.recipeapp.ui.screens.main.TextSearchViewModel
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule

abstract class BaseViewModelTest {
    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    protected lateinit var recipeRepository: RecipeRepository

    @Before
    open fun before() {
        recipeRepository = mockk()
    }
}