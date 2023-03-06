package com.pi.recipeapp.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.pi.recipeapp.R
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.animation.DisplayShimmerEffect
import com.pi.recipeapp.ui.scaffold_components.RecipeModalBottomSheet
import com.pi.recipeapp.ui.scaffold_components.showModalSheetState
import com.pi.recipeapp.ui.utils.UiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    provideFilterContentStates: () -> FilterContentStates,
    provideSearchInput: () -> String,
    provideRecipesState: () -> UiState<List<Recipe>>,
    onApplyFilterClick: () -> Unit,
    onSearchInputChange: (String) -> Unit,
    onFilterIngredientsMapChangeValue: (key: String) -> Unit,
    onFilterCategoriesMapChangeValue: (key: String) -> Unit,
    onFilterIngredientNameChangeValue: (name: String) -> Unit,
    navigateToDetailScreen: (Recipe) -> Unit,
    showSnackbar: (String) -> Unit
) {
    val recipeState = provideRecipesState()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
    )

    val coroutineScope = rememberCoroutineScope()

    RecipeModalBottomSheet(modalSheetState = modalSheetState, sheetContent = {
        FilterContent(
            provideFilterContentStates = provideFilterContentStates,
            onFilterIngredientNameChangeValue = onFilterIngredientNameChangeValue,
            onFilterCategoriesMapChangeValue = onFilterCategoriesMapChangeValue,
            onFilterIngredientsMapChangeValue = onFilterIngredientsMapChangeValue,
            onApplyFilterClick = {
                onApplyFilterClick()
                coroutineScope.showModalSheetState(modalSheetState)
            })
    }) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            RecipeTextField(
                recipeSearchInput = provideSearchInput,
                onValueChange = onSearchInputChange,
                showFilterSheet = {
                    coroutineScope.showModalSheetState(modalSheetState)
                }
            )
            Spacer(modifier = Modifier.padding(8.dp))

            if (recipeState.isLoading) {
                DisplayShimmerEffect()
            }
            recipeState.data?.let { recipes ->
                if (recipes.isEmpty())
                    showSnackbar(stringResource(id = R.string.empty_request_error))
                else
                    RecipeList(recipes, navigateToDetailScreen)
            }

            recipeState.errorMessage?.let { error ->
                showSnackbar(error)
            }

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RecipeTextField(
    recipeSearchInput: () -> String,
    onValueChange: (String) -> Unit,
    showFilterSheet: () -> Unit
) {
    var isCancelVisible by remember {
        mutableStateOf(false)
    }
    var isFilterVisible by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    val textInput = recipeSearchInput()
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            value = textInput,
            onValueChange = { textValue ->
                isFilterVisible = textValue.isNotEmpty()
                onValueChange(textValue)
            },
            label = { Text(text = "Enter name...") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "recipe search")
            },
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, false)
                .animateContentSize()
                .onFocusChanged { focusState ->
                    isCancelVisible = focusState.isFocused
                    if (focusState.isFocused && textInput.isNotEmpty()) {
                        isFilterVisible = true
                    } else if (!focusState.isFocused) {
                        isFilterVisible = false
                    }
                },
        )
        AnimatedVisibility(visible = isCancelVisible) {
            TextButton(
                onClick = { focusManager.clearFocus() }, modifier = Modifier
                    .weight(1f, false)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.subtitle1,
                )
            }
        }
        AnimatedVisibility(visible = isFilterVisible) {
            Icon(
                imageVector = Icons.Outlined.FilterAlt,
                contentDescription = "",
                modifier = Modifier
                    .weight(1f, false)
                    .size(25.dp)
                    .clickable {
                        showFilterSheet()
                    }
            )
        }
    }
}

@Composable
private fun RecipeList(recipes: List<Recipe>, onRecipeItemClick: (Recipe) -> Unit) {
    LazyVerticalGrid(modifier = Modifier.fillMaxWidth(), columns = GridCells.Fixed(2)) {
        items(recipes) {
            RecipeListItem(it, onRecipeItemClick)
        }
    }


}

@Composable
private fun RecipeListItem(recipe: Recipe, onRecipeItemClick: (Recipe) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = { onRecipeItemClick(recipe) }), shape = CutCornerShape(16.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.imageUrl)
                        .size(Size.ORIGINAL) // Set the target size to load the image at.
                        .build()
                ),
                contentDescription = ""
            )
            Text(
                recipe.name, modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MainScreenPreview() {

}
