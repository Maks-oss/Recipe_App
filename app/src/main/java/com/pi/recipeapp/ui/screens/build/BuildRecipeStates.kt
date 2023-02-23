package com.pi.recipeapp.ui.screens.build

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import com.pi.recipeapp.data.domain.Recipe

data class BuildRecipeStates(
    val recipe: Recipe? = null,
    val isExpanded: Boolean = false,
    val imageBitmap: Bitmap? = null,
    val imageUri: String? = null,
    val recipeName: String = "",
    val textInstruction: String = "",
    val videoInstruction: String = "",
    val ingredients: List<String> = emptyList(),
    val measures: List<String> = emptyList()
)