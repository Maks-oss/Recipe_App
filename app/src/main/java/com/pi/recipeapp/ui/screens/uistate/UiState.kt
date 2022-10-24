package com.pi.recipeapp.ui.screens.uistate

data class UiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
