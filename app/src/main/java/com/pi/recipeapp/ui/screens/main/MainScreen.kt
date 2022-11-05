package com.pi.recipeapp.ui.screens.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.pi.recipeapp.R
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.animation.DisplayShimmerEffect
import com.pi.recipeapp.ui.screens.uistate.UiState


@Composable
fun MainScreen(
    provideSearchInput: () -> String,
    provideRecipesState: () -> UiState<List<Recipe>>,
    onSearchInputChange: (String) -> Unit,
    navigateToDetailScreen: (Recipe) -> Unit,
    showSnackbar: (String) -> Unit
) {

    Column(
        Modifier
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        RecipeTextField(
            provideSearchInput,
            onValueChange = onSearchInputChange
        )
        Spacer(modifier = Modifier.padding(8.dp))

        Crossfade(targetState = provideRecipesState) { provideState ->
            val recipeState = provideState()
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

//        FloatingActionButton(onClick = {  }, modifier = Modifier.weight(1f,false)) {
//            Icon(imageVector = Icons.Filled.PhotoCamera, contentDescription = "")
//        }
    }
}

@Composable
private fun RecipeTextField(recipeSearchInput: () -> String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = recipeSearchInput(),
        onValueChange = onValueChange,
        label = { Text(text = "Enter ingredient...") },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "recipe search")
        },
        shape = CircleShape,
        modifier = Modifier
            .fillMaxWidth()
    )
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

//@Preview(
//    showBackground = true,
//    showSystemUi = true
//)
//@Composable
//fun MainScreenPreview() {
//
//    RecipeAppTheme {
//        MainScreen({})
//    }
//}