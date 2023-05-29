package com.pi.recipeapp.ui.scaffold_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.google.firebase.auth.FirebaseUser
import com.pi.recipeapp.R
import com.pi.recipeapp.utils.AppConstants

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecipeModalDrawerContent(
    user: FirebaseUser?,
    navigateToTextSearchScreen: () -> Unit = {},
    navigateToImageSearchScreen: () -> Unit = {},
    navigateToCreateRecipeScreen: () -> Unit = {},
    navigateToSavedRecipesScreen: () -> Unit = {},
    signOut: () -> Unit = {},
) {
    val userImage = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(user?.photoUrl ?: AppConstants.ANONYM_USER)
            .size(Size.ORIGINAL)
            .build()
    )
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomStart) {
            Image(
                painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.food_background_dark else R.drawable.food_background_light),
                contentDescription = "",
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Image(
                    painter = userImage,
                    contentDescription = "",
                    Modifier
                        .size(70.dp)
                        .clip(
                            CircleShape
                        )
                        .border(1.dp, MaterialTheme.colors.onSurface, CircleShape)
                )

                Text(
                    text = user?.email ?: "",
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        ListItem(
            icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "") },
            text = {
                Text(
                    text = "Text Search", style = MaterialTheme.typography.body1
                )
            },
            modifier = Modifier.clickable { navigateToTextSearchScreen() })
        ListItem(
            icon = { Icon(imageVector = Icons.Filled.ImageSearch, contentDescription = "") },
            text = {
                Text(
                    text = "Image Search", style = MaterialTheme.typography.body1
                )
            },
            modifier = Modifier.clickable { navigateToImageSearchScreen() })
        ListItem(icon = { Icon(imageVector = Icons.Filled.Edit, contentDescription = "") }, text = {
            Text(
                text = "Create Recipe", style = MaterialTheme.typography.body1
            )
        }, modifier = Modifier.clickable { navigateToCreateRecipeScreen() })
        ListItem(
            icon = { Icon(imageVector = Icons.Filled.Folder, contentDescription = "") },
            text = {
                Text(
                    text = "Saved Recipes", style = MaterialTheme.typography.body1
                )
            },
            modifier = Modifier.clickable { navigateToSavedRecipesScreen() })
        Column {
            Divider()
            ListItem(
                icon = { Icon(imageVector = Icons.Filled.Logout, contentDescription = "") },
                text = {
                    Text(
                        text = "Sign Out", style = MaterialTheme.typography.body1
                    )
                }, modifier = Modifier.clickable { signOut() })
        }
    }
}