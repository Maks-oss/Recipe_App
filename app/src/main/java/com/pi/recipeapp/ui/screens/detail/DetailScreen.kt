package com.pi.recipeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.animation.DisplayShimmerEffect
import com.pi.recipeapp.ui.animation.LoadingShimmerEffect

@Composable
fun DetailScreen(recipe: Recipe?) {
    Surface(
        shape = CutCornerShape(16.dp), modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(recipe?.imageUrl)
                .build(),
        )
        Column {
            if (painter.state is AsyncImagePainter.State.Loading || recipe?.imageUrl == null) {
                LoadingShimmerEffect(300.dp)
            }
            Image(
                painter = painter,
                contentScale = ContentScale.Crop,
                contentDescription = "",
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )


        }

    }
}

@Preview(showSystemUi = true)
@Composable
fun DetailScreenPreview() {
//    DetailScreen(Recipe("",AppConstants.IMAGE_NOT_FOUND_URL))
}