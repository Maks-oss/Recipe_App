package com.pi.recipeapp.ui.scaffold_components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pi.recipeapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RecipeTopAppBar(
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState,
    isItemsSelected: Boolean,
    onItemDeleteClick: () -> Unit
) {
    TopAppBar(title = {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "",
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        scaffoldState.drawerState.open()
                    }
                })
            Spacer(modifier = Modifier.padding(8.dp))
            Text(text = stringResource(id = R.string.app_name))
            Spacer(modifier = Modifier.weight(1f, true))
            AnimatedVisibility(visible = isItemsSelected) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "", modifier = Modifier.clickable(onClick = onItemDeleteClick))
            }
        }
    })
}