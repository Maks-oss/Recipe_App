package com.pi.recipeapp.ui.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.times
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.MainAxisAlignment
import com.google.accompanist.flowlayout.SizeMode
import com.pi.recipeapp.R
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.data.dto.Categories
import com.pi.recipeapp.ui.animation.DisplayShimmerEffect
import com.pi.recipeapp.ui.scaffold_components.RecipeModalBottomSheet
import com.pi.recipeapp.ui.scaffold_components.showModalSheetState
import com.pi.recipeapp.ui.screens.uistate.UiState
import com.pi.recipeapp.ui.utils.BlankTextField
import com.pi.recipeapp.ui.utils.MeasureUnconstrainedViewWidth
import com.pi.recipeapp.ui.utils.autoWidth
import com.pi.recipeapp.ui.utils.getSortedMapByBoolean


@Composable
fun MainScreen(
    ingredients: List<String>,
    categories: List<String>,
    provideSearchInput: () -> String,
    provideRecipesState: () -> UiState<List<Recipe>>,
    onSearchInputChange: (String) -> Unit,
    navigateToDetailScreen: (Recipe) -> Unit,
    showSnackbar: (String) -> Unit
) {

    Column(
        Modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        RecipeTextField(
            ingredients = ingredients,
            categories = categories,
            provideSearchInput,
            onValueChange = onSearchInputChange
        )
        Spacer(modifier = Modifier.padding(8.dp))

        val recipeState = provideRecipesState()
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RecipeTextField(
    ingredients: List<String>,
    categories: List<String>,
    recipeSearchInput: () -> String,
    onValueChange: (String) -> Unit,
    onFilterClick: () -> Unit = {}
) {
    var isFilterVisible by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
    )
    val coroutineScope = rememberCoroutineScope()
    RecipeModalBottomSheet(modalSheetState = modalSheetState, sheetContent = {
        FilterContent(ingredients = ingredients, categories = categories, onApplyFilterClick = {
            coroutineScope.showModalSheetState(modalSheetState)
        })
    }) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = recipeSearchInput(),
                onValueChange = onValueChange,
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
                        isFilterVisible = focusState.isFocused
                    },
            )
            AnimatedVisibility(visible = isFilterVisible) {
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
                            coroutineScope.showModalSheetState(modalSheetState)
                            onFilterClick()
                        }
                )
            }
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

@Composable
fun FilterContent(
    ingredients: List<String>,
    categories: List<String>,
    onApplyFilterClick: () -> Unit
) {
    val ingredientsMap = remember {
        mutableMapOf<String, Boolean>()
    }
    if (ingredientsMap.isEmpty()) {
        ingredientsMap.putAll(ingredients.map { it to false })
    }
    var ingredientName by remember {
        mutableStateOf("")
    }
    val specificIngredients by remember(ingredientName) {
        derivedStateOf {
            ingredientsMap.filter { it.key.contains(ingredientName, ignoreCase = true) }
        }
    }
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Filter", style = MaterialTheme.typography.h5)
            OutlinedButton(
                onClick = { /*TODO*/ }, shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colors.primary)
            ) {
                Text(text = "Apply filter")
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = "Category", style = MaterialTheme.typography.h6)

        CustomChipsGrid(elements = categories, elementsPerRow = 5)
        Divider(Modifier.padding(8.dp))
        Text(text = "Ingredients", style = MaterialTheme.typography.h6)

        BlankTextField(
            text = ingredientName,
            label = "Enter Ingredient...",
            textStyle = MaterialTheme.typography.body1,
            onTextChange = { ingredientName = it; })
        CustomChipsGrid(
            elements = if (ingredientName.isEmpty()) ingredientsMap.getSortedMapByBoolean().keys.toList() else specificIngredients.getSortedMapByBoolean().keys.toList(),
            elementsPerRow = 8,
            onChipClick = { chip ->
               ingredientsMap[chip] = true
            })

        Spacer(modifier = Modifier.padding(8.dp))
    }
}

@Composable
private fun CustomChipsGrid(
    modifier: Modifier = Modifier,
    elements: List<String>,
    elementsPerRow: Int,
    chipSpacing: Dp = 4.dp,
    onChipClick: ((String) -> Unit)? = null,
) {
    if (elements.isNotEmpty()) {
        val numRows = remember(elements.size, elementsPerRow) {
            (elements.size + elementsPerRow - 1) / elementsPerRow
        }

        Column(
            modifier = modifier
                .height(numRows * 40.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            for (row in 0 until numRows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(chipSpacing)
                ) {
                    for (col in 0 until elementsPerRow) {
                        val index = row * elementsPerRow + col
                        if (index < elements.size) {
                            CustomFilterChip(
                                text = elements[index],
                                onChipClick = { onChipClick?.invoke(elements[index]) }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun CustomFilterChip(text: String, onChipClick: (() -> Unit)) {
    var selected by remember {
        mutableStateOf(false)
    }
    FilterChip(selected = selected,
        onClick = { selected = !selected; onChipClick() },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Localized Description"
                )
            }
        } else {
            null
        },
        colors = ChipDefaults.filterChipColors(selectedBackgroundColor = MaterialTheme.colors.secondary)) {
        Text(text = text)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MainScreenPreview() {

}
