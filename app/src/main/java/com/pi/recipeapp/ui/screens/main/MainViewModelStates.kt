package com.pi.recipeapp.ui.screens.main

import kotlinx.coroutines.Job

data class MainViewModelStates(
    val recipeSearchInput: String = "",
    val job: Job? = null
)