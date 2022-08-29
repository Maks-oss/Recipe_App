package com.pi.recipeapp.ui.screens

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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.theme.RecipeAppTheme
import com.pi.recipeapp.utils.AppConstants
import kotlinx.coroutines.*
//
//private val testRecipeList = listOf(
//    Recipe("name 1", AppConstants.IMAGE_NOT_FOUND_URL),
//    Recipe("name 2", AppConstants.IMAGE_NOT_FOUND_URL),
//    Recipe("name 3", AppConstants.IMAGE_NOT_FOUND_URL),
//    Recipe("name 4", AppConstants.IMAGE_NOT_FOUND_URL),
//)

@Composable
fun MainScreen(mainViewModel: MainViewModel) {
    Column(
        Modifier
            .padding(8.dp)
    ) {
        RecipeTextField(mainViewModel.recipeSearchInput, onValueChange = mainViewModel::onRecipeSearchInputChange)
        Spacer(modifier = Modifier.padding(8.dp))
        RecipeList(mainViewModel.recipesState)
    }
}

@Composable
private fun RecipeTextField(recipeSearchInput: String,onValueChange:(String)->Unit) {
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
private fun RecipeList(recipes: List<Recipe>) {

    LazyVerticalGrid(modifier = Modifier.fillMaxWidth(), columns = GridCells.Fixed(2)) {
        items(recipes) {
            RecipeListItem(it)
        }
    }
}

@Composable
private fun RecipeListItem(recipe: Recipe) {
    Card(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(), shape = CutCornerShape(16.dp)) {
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
                    .fillMaxWidth())
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