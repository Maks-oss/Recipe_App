package com.pi.recipeapp.ui.animation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LoadingShimmerEffect(height: Dp = 200.dp) {

    //These colors will be used on the brush. The lightest color should be in the middle

    val gradient = listOf(
        Color.LightGray.copy(alpha = 0.6f), //darker grey (90% opacity)
        Color.LightGray.copy(alpha = 0.3f), //lighter grey (30% opacity)
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition() // animate infinite times

    val translateAnimation = transition.animateFloat( //animate the transition
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, // duration for the animation
                easing = FastOutLinearInEasing
            ), repeatMode = RepeatMode.Reverse
        )
    )
    val brush = linearGradient(
        colors = gradient,
        start = Offset(10f, 10f),
        end = Offset(
            x = translateAnimation.value,
            y = translateAnimation.value
        )
    )
    ShimmerGridItem(brush = brush, height = height)
}

@Composable
fun ShimmerGridItem(brush: Brush, height: Dp) {
    Spacer(
        modifier = Modifier
            .height(height)
            .padding(8.dp)
            .fillMaxWidth()
            .clip(CutCornerShape(16.dp))
            .background(brush)
    )
}

@Composable
fun DisplayShimmerEffect(itemsCount: Int = 10) {
    LazyVerticalGrid(modifier = Modifier.fillMaxWidth(), columns = GridCells.Fixed(2)) {
        items(itemsCount) {
            LoadingShimmerEffect()
        }
    }
}
