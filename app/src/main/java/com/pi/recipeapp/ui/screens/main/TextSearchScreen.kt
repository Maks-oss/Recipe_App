package com.pi.recipeapp.ui.screens.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.pi.recipeapp.R
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.animation.DisplayShimmerEffect
import com.pi.recipeapp.ui.theme.ComplexRoundedShape
import com.pi.recipeapp.ui.utils.UiState

@Composable
fun MainScreen(
    provideSearchInput: () -> String,
    provideRecipesState: () -> UiState<List<Recipe>>,
    onSearchInputChange: (String) -> Unit,
    navigateToDetailScreen: (Recipe) -> Unit,
    showSnackbar: (String) -> Unit
) {
    val recipeState = provideRecipesState()
    Column(
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        RecipeTextField(
            recipeSearchInput = provideSearchInput,
            onValueChange = onSearchInputChange,
        )
        Spacer(modifier = Modifier.padding(8.dp))

        if (recipeState.isLoading) {
            DisplayShimmerEffect()
        }
        recipeState.data?.let { recipes ->
            if (recipes.isEmpty())
                showSnackbar(stringResource(id = R.string.empty_request_error))
            else
                RecipeGridList(
                    Modifier.fillMaxSize(),
                    recipes,
                    onRecipeItemClick = navigateToDetailScreen
                )
        }

        recipeState.errorMessage?.let { error ->
            showSnackbar(error)
        }

    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RecipeTextField(
    recipeSearchInput: () -> String,
    onValueChange: (String) -> Unit,
) {
    val textInput = recipeSearchInput()

    OutlinedTextField(
        value = textInput,
        onValueChange = { textValue ->
            onValueChange(textValue)
        },
        label = { Text(text = "Enter name...") },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "recipe search")
        },
        shape = CircleShape,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    )


}

@Composable
fun RecipeGridList(
    modifier: Modifier = Modifier,
    recipes: List<Recipe>,
    onRecipeItemClick: (Recipe) -> Unit,
    selectedRecipes: Map<Recipe, Boolean> = emptyMap(),
    onRecipeItemLongClick: ((Recipe, isSelected: Boolean) -> Unit)? = null
) {
    LazyVerticalGrid(modifier = modifier, columns = GridCells.Fixed(2)) {
        items(recipes) {
            val isSelected = selectedRecipes[it] ?: false
            RecipeListItem(
                it,
                onRecipeItemClick = onRecipeItemClick,
                onRecipeItemLongClick = onRecipeItemLongClick,
                isSelected = isSelected
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeListItem(
    recipe: Recipe,
    onRecipeItemClick: (Recipe) -> Unit,
    isSelected: Boolean = false,
    onRecipeItemLongClick: ((Recipe, isSelected: Boolean) -> Unit)? = null
) {
//    val isSelected = recipe.isSelected
    val selectedColor = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
    Card(
        backgroundColor = if (isSelected) selectedColor else MaterialTheme.colors.surface,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onRecipeItemClick(recipe) },
                onLongClick = {
//                    isSelected = !isSelected
                    onRecipeItemLongClick?.invoke(
                        recipe,
                        !isSelected
                    )
                }),
        shape = ComplexRoundedShape
    ) {

        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.imageUrl)
                        .size(Size.ORIGINAL)
                        .build()
                ),
                contentDescription = "",
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = recipe.name, modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.body1
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
