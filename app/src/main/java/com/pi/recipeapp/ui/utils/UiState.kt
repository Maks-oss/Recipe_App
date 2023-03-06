package com.pi.recipeapp.ui.utils

data class UiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
