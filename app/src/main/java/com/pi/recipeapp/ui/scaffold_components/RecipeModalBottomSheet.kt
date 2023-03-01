package com.pi.recipeapp.ui.scaffold_components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.screens.DetailScreen
import com.pi.recipeapp.utils.AppConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecipeModalBottomSheet(modalSheetState: ModalBottomSheetState,sheetContent: @Composable () -> Unit, content: @Composable () -> Unit) {
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = CutCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            sheetContent()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun CoroutineScope.showModalSheetState(modalSheetState: ModalBottomSheetState) {
    launch {
        if (modalSheetState.isVisible)
            modalSheetState.hide()
        else
            modalSheetState.animateTo(ModalBottomSheetValue.Expanded)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showSystemUi = true)
@Composable
fun RecipeModalBottomSheetPreview() {
    val recipe = Recipe(
        name = "Some Recipe",
        imageUrl = AppConstants.IMAGE_NOT_FOUND_URL,
        instruction = "test\n".repeat(3),
        ingredients = mapOf("test ingredient" to "3")
    )
    var isExpanded by remember {
        mutableStateOf(false)
    }
//    RecipeModalBottomSheet(sheetContent = {}, modalSheetState = rem) {
//        DetailScreen(recipe = recipe, provideExpandedValue = { isExpanded }, onExpandClick = { isExpanded = !isExpanded })
//    }

}