package com.pi.recipeapp.ui.screens.imagesearch

import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.pi.recipeapp.R
import com.pi.recipeapp.ml.LiteModelAiyVisionClassifierFoodV11
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage

@Composable
fun RecipeImageSearchScreen() {
    var imageBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    val context = LocalContext.current
    val takePictureFromFolder =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            imageBitmap = MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                it
            )
        }
    val takePictureFromCamera =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            imageBitmap = it
        }
    var recipeName by remember {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
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
            withContext(Dispatchers.Default) {
                if (imageBitmap != null) {
                    val model = LiteModelAiyVisionClassifierFoodV11.newInstance(context)

                    val image = TensorImage.fromBitmap(
                        imageBitmap
                    )
                    val outputs = model.process(image)
                    val probability = outputs.probabilityAsCategoryList
                    val recipe = probability.maxByOrNull { it.score }?.label ?: ""
                    recipeName = recipe
                    model.close()
                }
            }
        }

        Text(text = recipeName)
        Spacer(modifier = Modifier.padding(8.dp))
        ButtonWithIcon(icon = Icons.Filled.PhotoCamera, text = "Take recipe photo") {
            takePictureFromCamera.launch()
        }
        ButtonWithIcon(icon = Icons.Filled.Folder, text = "Take recipe from folder") {
            takePictureFromFolder.launch("image/*")
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