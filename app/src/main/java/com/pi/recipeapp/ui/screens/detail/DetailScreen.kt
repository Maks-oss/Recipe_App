package com.pi.recipeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.utils.AppConstants

@Composable
fun DetailScreen(recipe: Recipe) {
    Surface(shape = CutCornerShape(16.dp), modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.imageUrl)
                        .build()
                ),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
        }
    }
}
@Preview(showSystemUi = true)
@Composable
fun DetailScreenPreview(){
    DetailScreen(Recipe("",AppConstants.IMAGE_NOT_FOUND_URL))
}