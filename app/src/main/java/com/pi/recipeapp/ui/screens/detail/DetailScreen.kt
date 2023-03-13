package com.pi.recipeapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.utils.CreateExpandedItem
import com.pi.recipeapp.ui.utils.CustomSurface
import com.pi.recipeapp.ui.utils.CustomTabs
import com.pi.recipeapp.ui.utils.VideoPlayer
import com.pi.recipeapp.utils.InstructionTabsConstants
import com.pi.recipeapp.utils.Routes

@Composable
fun DetailScreen(
    recipe: Recipe?,
    provideExpandedValue: () -> Boolean,
    onExpandClick: () -> Unit,
    onFavouritesClick: (Recipe) -> Unit,
    isPreview: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Detail(
            recipe = recipe,
            provideExpandedValue = provideExpandedValue,
            onExpandClick = onExpandClick,
            isPreview = isPreview,
            onFavouritesClick = onFavouritesClick
        )
        Instructions(recipe)

    }
}

@Composable
fun CreatedRecipeDetailPreview(
    recipe: Recipe?,
    provideExpandedValue: () -> Boolean,
    onExpandClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Column {
        Detail(
            recipe = recipe,
            provideExpandedValue = provideExpandedValue,
            onExpandClick = onExpandClick,
            isPreview = true
        )
        Instructions(recipe)
        OutlinedButton(
            onClick = onConfirmClick,
            Modifier.padding(8.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Text(text = "Confirm")
        }
    }
}


@Composable
fun Detail(
    recipe: Recipe?,
    provideExpandedValue: () -> Boolean,
    onExpandClick: () -> Unit,
    onFavouritesClick: (Recipe) -> Unit = { },
    isPreview: Boolean = false
) {
    CustomSurface {

        Column {
            RecipeImage(recipe)
            if (recipe != null) {
                val isExpandedList = provideExpandedValue()
                Title(isExpandedList, recipe, onExpandClick)
                if (!isPreview) {
                    TextButton(onClick = { onFavouritesClick(recipe) }) {
                        Row {
                            Icon(imageVector = Icons.Outlined.Favorite, contentDescription = "")
                            Spacer(modifier = Modifier.padding(8.dp))
                            Text(text = "Add to favourites")
                        }
                    }
                }
                Ingredients(recipe = recipe, isVisible = isExpandedList)
            }
        }
    }
}

@Composable
private fun Instructions(
    recipe: Recipe?
) {
    CustomSurface {
        var tabsState by remember {
            mutableStateOf(0)
        }
        Column {
            CustomTabs(
                titles = listOf("Text Instruction", "Video Instruction"),
                state = tabsState
            ) { index ->
                tabsState = index
            }
            Crossfade(targetState = tabsState) { tabsState ->
                when (tabsState) {
                    InstructionTabsConstants.TEXT_INSTRUCTION -> Text(
                        text = recipe?.instruction ?: "",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(8.dp)
                    )
                    InstructionTabsConstants.VIDEO_INSTRUCTION -> if (!recipe?.videoLink.isNullOrEmpty()) VideoPlayer(
                        uri = recipe?.videoLink,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

        }

    }

}

@Composable
private fun RecipeImage(recipe: Recipe?) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(recipe?.imageUrl)
            .build(),
    )

    Image(
        painter = painter,
        contentScale = ContentScale.Crop,
        contentDescription = "",
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Composable
private fun Title(
    isExpanded: Boolean,
    recipe: Recipe,
    onExpandClick: () -> Unit
) {
    CreateExpandedItem(text = recipe.name, isExpanded = isExpanded) {
        onExpandClick()
    }
}

@Composable
private fun Ingredients(recipe: Recipe, isVisible: Boolean) {
    val ingredients = recipe.ingredients
    AnimatedVisibility(visible = isVisible) {
        FlowRow(
            Modifier
                .padding(8.dp)
        ) {
            for ((ingredient, measure) in ingredients) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://www.themealdb.com/images/ingredients/${ingredient}.png")
                            .crossfade(true)
                            .build(),
                    )

                    Image(
                        painter = painter,
                        contentScale = ContentScale.Crop,
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp)
                    )
                    Text(
                        text = ingredient,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.subtitle1
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = measure, modifier = Modifier
                            .padding(8.dp), style = MaterialTheme.typography.subtitle1
                    )
                }
            }

        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DetailScreenPreview() {

}