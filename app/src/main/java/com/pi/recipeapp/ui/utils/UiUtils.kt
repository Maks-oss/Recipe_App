package com.pi.recipeapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun CreateExpandedItem(text: String, isExpanded: Boolean, onExpandClick: () -> Unit) {
    var textHeight by remember { mutableStateOf(0) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.onGloballyPositioned {
                textHeight = it.size.height
            }, overflow = TextOverflow.Ellipsis)
        Icon(
            imageVector = if (!isExpanded) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess,
            contentDescription = "",
            modifier = Modifier
                .size(with(LocalDensity.current) { (textHeight).toDp() })
                .clickable { onExpandClick() }
        )
    }
}

@Composable
fun CustomSurface(content: @Composable () -> Unit) {
    Surface(
        shape = CutCornerShape(16.dp), modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(), elevation = 1.dp
    ) {
        content()
    }
}

@Composable
fun InstructionTabs(state: Int, onTabClick: (index: Int) -> Unit) {
    val titles = listOf("Text instruction", "Video instruction")
    TabRow(selectedTabIndex = state, backgroundColor = MaterialTheme.colors.surface, contentColor = MaterialTheme.colors.primary) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = state == index,
                onClick = {
                    onTabClick(index)
                },
                text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colors.onSurface) }
            )
        }
    }
}