package com.pi.recipeapp.ui.screens.imagesearch

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.google.accompanist.flowlayout.FlowRow
import com.pi.recipeapp.R
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ml.LiteModelAiyVisionClassifierFoodV11
import com.pi.recipeapp.ui.utils.UiState
import com.pi.recipeapp.ui.utils.CustomSurface
import com.pi.recipeapp.ui.utils.HyperlinkText
import org.tensorflow.lite.support.image.TensorImage

@Composable
fun RecipeImageSearchScreen(
    provideRecipesImageSearchState: () -> UiState<List<Recipe>>,
    imageSearchStates: ImageSearchStates,
    changeImageBitmap: (Bitmap?) -> Unit,
    changeRecipeName: (String) -> Unit,
    navigateToDetailScreen: (Recipe) -> Unit,
    loadRecipes: suspend (String) -> Unit
) {
    val recipesImageSearchState = provideRecipesImageSearchState()
    val (recipeName, imageBitmap) = imageSearchStates
    val context = LocalContext.current
    val takePictureFromFolder =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                changeImageBitmap(
                    MediaStore.Images.Media.getBitmap(
                        context.contentResolver,
                        uri
                    )
                )
            }
        }
    val takePictureFromCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            changeImageBitmap(bitmap)
        }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        CustomSurface {
            Column(
                modifier = Modifier
                    .padding(8.dp),
            ) {
                Image(
                    painter = if (imageBitmap != null) rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageBitmap)
                            .crossfade(true)
                            .size(Size.ORIGINAL)
                            .build()
                    ) else painterResource(id = R.drawable.image_placeholder),
                    contentDescription = "",
                    modifier = Modifier
                        .size(height = 400.dp, width = LocalConfiguration.current.screenWidthDp.dp)
                        .align(Alignment.CenterHorizontally)
                )

                LaunchedEffect(imageBitmap) {
                    try {
                        changeRecipeName(getTFSearchResult(imageBitmap, context))
                    } catch (exc: Exception) {
                        Toast.makeText(context, exc.message, Toast.LENGTH_LONG).show()
                        changeRecipeName("")
                    }
                }

                if (recipeName.isNotEmpty()) {
                    LaunchedEffect(recipeName) {
                        loadRecipes(recipeName)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = recipeName, style = MaterialTheme.typography.h6)
                        HyperlinkText(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            fullText = "See on website",
                            linkText = listOf("See on website"),
                            hyperlinks = listOf("https://www.allrecipes.com/search?q=$recipeName")
                        )
                    }
                    if (recipesImageSearchState.isLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxSize()
                        )
                    } else {
                        ShowRecipeResults(recipesImageSearchState, navigateToDetailScreen)
                    }

                }
            }
        }
        ImageSearchButtons(takePictureFromCamera, takePictureFromFolder)
    }
}

@Composable
private fun ShowRecipeResults(
    recipesState: UiState<List<Recipe>>,
    navigateToDetailScreen: (Recipe) -> Unit
) {
    FlowRow {
        recipesState.data?.forEach { recipe ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = recipe.name, style = MaterialTheme.typography.subtitle1)
                OutlinedButton(
                    onClick = { navigateToDetailScreen(recipe) },
                    shape = CircleShape,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colors.primary
                    )
                ) {
                    Text(text = "Show Recipe")
                }
            }
        }
    }
}


private fun getTFSearchResult(
    imageBitmap: Bitmap?,
    context: Context,
): String {
    var recipeName = ""
    if (imageBitmap != null) {
        val model = LiteModelAiyVisionClassifierFoodV11.newInstance(context)
        val image = TensorImage.fromBitmap(
            imageBitmap
        )
        val outputs = model.process(image)
        val probability = outputs.probabilityAsCategoryList
        val maxProbabilityItem = probability.maxByOrNull { it.score }
        val score = maxProbabilityItem?.score ?: 0
        if (score.toDouble() < 0.5) {
            throw Exception("Unrecognized image. Please provide more detailed image")
        }
        val recipe = maxProbabilityItem?.label ?: ""
        recipeName = recipe
        model.close()
    }
    return recipeName
}

@Composable
private fun ImageSearchButtons(
    takePictureFromCamera: ManagedActivityResultLauncher<Void?, Bitmap?>,
    takePictureFromFolder: ManagedActivityResultLauncher<String, Uri?>
) {
    Row(modifier = Modifier.padding(8.dp)) {
        Button(
            onClick = { takePictureFromCamera.launch() },
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Row {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "")
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = "Take photo")
            }
        }
        Spacer(modifier = Modifier.padding(4.dp))
        OutlinedButton(
            onClick = { takePictureFromFolder.launch("image/*") },
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Row {
                Icon(imageVector = Icons.Default.Folder, contentDescription = "")
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = "Take from folder")
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun RecipeImageSearchScreenPreview() {
//    RecipeImageSearchScreen(UiState(), {}, { true })
}