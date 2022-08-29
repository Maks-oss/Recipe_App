package com.pi.recipeapp.ui.screens

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.animation.DisplayShimmerEffect



@Composable
fun MainScreen(mainViewModel: MainViewModel) {
    val recipeState = mainViewModel.recipesState
    Column(
        Modifier
            .padding(8.dp)
    ) {
        RecipeTextField(
            mainViewModel.recipeSearchInput,
            onValueChange = mainViewModel::onRecipeSearchInputChange
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Crossfade(targetState = mainViewModel.recipesState.isLoading) { isRecipesLoading ->
            if (isRecipesLoading) {
                DisplayShimmerEffect()
            } else if (recipeState.errorMessage != null) {
                Toast.makeText(LocalContext.current, stringResource(id = recipeState.errorMessage), Toast.LENGTH_SHORT).show()
            } else {
                RecipeList(recipeState.data)
            }
        }
    }
}

@Composable
private fun RecipeTextField(recipeSearchInput: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = recipeSearchInput,
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
private fun RecipeList(recipes: List<Recipe>?) {
    recipes?.let { recipes ->
        LazyVerticalGrid(modifier = Modifier.fillMaxWidth(), columns = GridCells.Fixed(2)) {
            items(recipes) {
                RecipeListItem(it)
            }
        }
    }

}

@Composable
private fun RecipeListItem(recipe: Recipe) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(), shape = CutCornerShape(16.dp)
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