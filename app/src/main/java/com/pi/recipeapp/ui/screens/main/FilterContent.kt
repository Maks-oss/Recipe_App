package com.pi.recipeapp.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.pi.recipeapp.ui.scaffold_components.showModalSheetState
import com.pi.recipeapp.ui.utils.BlankTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterContent(
    provideFilterContentStates: () -> FilterContentStates,
    onFilterIngredientsMapChangeValue: (key: String) -> Unit,
    onFilterCategoriesMapChangeValue: (key: String) -> Unit,
    onFilterIngredientNameChangeValue: (name: String) -> Unit,
    onApplyFilterClick: () -> Unit
) {
    val filterContentStates = provideFilterContentStates()
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Filter", style = MaterialTheme.typography.h5)
            OutlinedButton(
                onClick = { onApplyFilterClick() }, shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colors.primary)
            ) {
                Text(text = "Apply filter")
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = "Category", style = MaterialTheme.typography.h6)

        CustomFilterChipsGrid(
            elements = filterContentStates.categoriesMap,
            elementsPerRow = 5,
            onChipClick = onFilterCategoriesMapChangeValue
        )
        Divider(Modifier.padding(8.dp))
        Text(text = "Ingredients", style = MaterialTheme.typography.h6)

        BlankTextField(
            text = filterContentStates.ingredientName,
            label = "Enter Ingredient...",
            textStyle = MaterialTheme.typography.body1,
            onTextChange = onFilterIngredientNameChangeValue
        )
        CustomFilterChipsGrid(
            elements = filterContentStates.ingredientsMap,
            elementsPerRow = 8,
            onChipClick = onFilterIngredientsMapChangeValue
        )

        Spacer(modifier = Modifier.padding(8.dp))
    }
}

@Composable
private fun CustomFilterChipsGrid(
    modifier: Modifier = Modifier,
    elements: Map<String, Boolean>,
    elementsPerRow: Int,
    chipSpacing: Dp = 4.dp,
    onChipClick: ((String) -> Unit)? = null,
) {
    if (elements.isNotEmpty()) {
        val numRows = remember(elements.size, elementsPerRow) {
            (elements.size + elementsPerRow - 1) / elementsPerRow
        }

        Column(
            modifier = modifier
                .height(numRows * 40.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            for (row in 0 until numRows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(chipSpacing)
                ) {
                    for (col in 0 until elementsPerRow) {
                        val index = row * elementsPerRow + col
                        if (index < elements.size) {
                            val element = elements.toList()[index]
                            CustomFilterChip(
                                text = element.first,
                                isSelected = element.second,
                                onChipClick = {
                                    onChipClick?.invoke(element.first)
                                }
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun CustomFilterChip(text: String, isSelected: Boolean, onChipClick: (() -> Unit)) {
    FilterChip(selected = isSelected,
        onClick = { onChipClick() },
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Localized Description"
                )
            }
        } else {
            null
        },
        colors = ChipDefaults.filterChipColors(selectedBackgroundColor = MaterialTheme.colors.secondary)) {
        Text(text = text, style = MaterialTheme.typography.body2)
    }
}
