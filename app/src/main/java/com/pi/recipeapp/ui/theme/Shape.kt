package com.pi.recipeapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)

val ComplexRoundedShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val topStart = density.run { 32.dp.toPx() }
        val bottomEnd = density.run { 32.dp.toPx() }
        val bottomStart = density.run { 16.dp.toPx() }
        val rect = Rect(
            0f,
            0f,
            size.width,
            size.height
        )

        val radii = arrayOf(
            CornerRadius(topStart, topStart),
            CornerRadius(0f, 0f),
            CornerRadius(0f, 0f),
            CornerRadius(bottomEnd, bottomEnd),

            )

        val roundRect = RoundRect(
            rect,
            topLeft = radii[0],
            topRight = radii[1],
            bottomLeft = radii[2],
            bottomRight = radii[3]
        )
        val path = Path().apply {
            addRoundRect(roundRect)
            lineTo(size.width / 2 + bottomStart, size.height)
            lineTo(size.width, size.height / 2)
            close()
        }
        return Outline.Generic(path)
    }
}
