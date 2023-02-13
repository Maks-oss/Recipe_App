package com.pi.recipeapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.animation.LoadingShimmerEffect
import com.pi.recipeapp.ui.utils.CreateExpandedItem
import com.pi.recipeapp.ui.utils.CustomSurface
import com.pi.recipeapp.ui.utils.InstructionTabs
import com.pi.recipeapp.utils.InstructionTabsConstants

@Composable
fun DetailScreen(
    recipe: Recipe?,
    provideExpandedList: () -> List<Boolean>,
    onExpandClick: (index: Int) -> Unit
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
        Instructions(recipe, provideExpandedList, onExpandClick)
    }

//        Instructions(recipe, isExpanded[1], onExpandClick)
}


@Composable
fun Detail(
    recipe: Recipe?,
    provideExpandedList: () -> List<Boolean>,
    onExpandClick: (index: Int) -> Unit
) {
    CustomSurface {

        Column {
            RecipeImage(recipe)
            if (recipe != null) {
                val isExpandedList = provideExpandedList()
                Title(isExpandedList, recipe, onExpandClick)
                Ingredients(recipe = recipe, isVisible = isExpandedList.first())
            }
        }
    }
}

@Composable
private fun Instructions(
    recipe: Recipe?,
    provideExpandedList: () -> List<Boolean>,
    onExpandClick: (index: Int) -> Unit
) {
    CustomSurface {
//        val isExpandedList = provideExpandedList()
//        Column {
//            CreateExpandedItem(text = "Cooking instructions", isExpanded = isExpandedList[1]) {
//                onExpandClick(1)
//            }
//            OutlinedButton(onClick = { /*TODO*/ }, modifier = Modifier.padding(8.dp)) {
//                Text(text = "Show video instruction")
//            }
//            if (recipe != null) {
//                AnimatedVisibility(visible = isExpandedList[1]) {
//                    Text(text = recipe.instruction, style = MaterialTheme.typography.body1, modifier = Modifier.padding(8.dp))
//                }
//            }
//        }
//        Tab(selected = , onClick = { /*TODO*/ }) {
//
//        }
        var tabsState by remember {
            mutableStateOf(0)
        }
        Column {
            InstructionTabs(
                state = tabsState
            ) { index ->
                tabsState = index
            }
            when (tabsState) {
                InstructionTabsConstants.TEXT_INSTRUCTION -> Text(
                    text = recipe?.instruction ?: "",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(8.dp)
                )
                InstructionTabsConstants.VIDEO_INSTRUCTION -> Text(
                    text = "Video Instruction",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(8.dp)
                )
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
    if (painter.state is AsyncImagePainter.State.Loading || recipe?.imageUrl == null) {
        LoadingShimmerEffect(300.dp)
    }

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
    isExpandedList: List<Boolean>,
    recipe: Recipe,
    onExpandClick: (index: Int) -> Unit
) {
    CreateExpandedItem(text = recipe.name, isExpanded = isExpandedList.first()) {
        onExpandClick(0)
    }
}

@Composable
private fun Ingredients(recipe: Recipe, isVisible: Boolean) {
    val ingredients = recipe.ingredients
    AnimatedVisibility(visible = isVisible) {
        LazyColumn(Modifier.padding(8.dp)) {
            for ((ingredient, measure) in ingredients) {
                item {
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
                                .height(50.dp)
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
}

@Preview(showSystemUi = true)
@Composable
fun DetailScreenPreview() {

}