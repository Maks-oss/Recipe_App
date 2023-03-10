package com.pi.recipeapp.ui.screens.main

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
data class FilterContentStates(
    val ingredientsMap: Map<String, Boolean> = emptyMap(),
    val categoriesMap: Map<String, Boolean> = emptyMap(),
    val ingredientName: String = ""
)
