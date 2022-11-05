package com.pi.recipeapp.ui.screens.imagesearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pi.recipeapp.R

@Composable
fun RecipeImageSearchScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.image_placeholder),
            contentDescription = "",
            modifier = Modifier
                .size(height = 400.dp, width = LocalConfiguration.current.screenWidthDp.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        ButtonWithIcon(icon = Icons.Filled.PhotoCamera, text = "Take recipe photo") {

        }
        ButtonWithIcon(icon = Icons.Filled.Folder, text = "Take recipe from folder") {

        }


    }
}

@Composable
private fun ButtonWithIcon(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = icon, contentDescription = "")
            Text(text = text, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun RecipeImageSearchScreenPreview() {
    RecipeImageSearchScreen()
}