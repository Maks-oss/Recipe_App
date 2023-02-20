package com.pi.recipeapp.ui.screens

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.animation.LoadingShimmerEffect
import com.pi.recipeapp.ui.utils.CreateExpandedItem
import com.pi.recipeapp.ui.utils.CustomSurface
import com.pi.recipeapp.ui.utils.InstructionTabs
import com.pi.recipeapp.ui.utils.VideoPlayer
import com.pi.recipeapp.utils.InstructionTabsConstants
import org.w3c.dom.Text

@Composable
fun DetailScreen(
    recipe: Recipe?,
    provideExpandedList: () -> Boolean,
    onExpandClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Detail(
            recipe = recipe,
            provideExpandedList = provideExpandedList,
            onExpandClick = onExpandClick
        )
        Instructions(recipe)
    }
}


@Composable
fun Detail(
    recipe: Recipe?,
    provideExpandedList: () -> Boolean,
    onExpandClick: () -> Unit
) {
    CustomSurface {

        Column {
            RecipeImage(recipe)
            if (recipe != null) {
                val isExpandedList = provideExpandedList()
                Title(isExpandedList, recipe, onExpandClick)
                TextButton(onClick = {  }) {
                    Row {
                        Icon(imageVector = Icons.Outlined.Favorite, contentDescription = "")
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(text = "Add to favourites")
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
            InstructionTabs(
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
                    InstructionTabsConstants.VIDEO_INSTRUCTION -> if (!recipe?.videoLink.isNullOrEmpty()) VideoPlayer(uri = recipe?.videoLink, modifier = Modifier.fillMaxSize())
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