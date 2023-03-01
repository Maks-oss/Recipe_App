package com.pi.recipeapp.ui.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout

fun Modifier.autoWidth() = composed {
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }
}

fun<K> Map<K,Boolean>.getSortedMapByBoolean(booleanValue: Boolean = true): Map<K, Boolean> {
    return this.toList().sortedBy { booleanValue }.toMap()
}