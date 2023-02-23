package com.pi.recipeapp.ui.screens.build

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
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
import com.pi.recipeapp.R
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.ui.scaffold_components.RecipeModalBottomSheet
import com.pi.recipeapp.ui.scaffold_components.showModalSheetState
import com.pi.recipeapp.ui.screens.RecipeDetailPreview
import com.pi.recipeapp.ui.utils.BlankTextField
import com.pi.recipeapp.ui.utils.CustomSurface
import com.pi.recipeapp.ui.utils.CustomTabs
import com.pi.recipeapp.utils.InstructionTabsConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BuildRecipeScreen(
    buildRecipeStates: BuildRecipeStates,
    applyRecipe: (Recipe) -> Unit,
    onExpandValueChange: () -> Unit,
    onRecipeNameTextChange: (String) -> Unit,
    onRecipeImageChange: (Bitmap?) -> Unit,
    onRecipeUriChange: (String) -> Unit,
    onIngredientsAndMeasuresAdd: (value: String) -> Unit,
    onIngredientChange: (index: Int, String) -> Unit,
    onMeasureChange: (index: Int, String) -> Unit,
    onIngredientsAndMeasuresRemove: (index: Int) -> Unit,
    onTextInstructionChange: (String) -> Unit,
    onVideoInstructionChange: (String) -> Unit,
    // TODO when database will be present
    onConfirmClick: () -> Unit = {}
) {
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
    )
    val coroutineScope = rememberCoroutineScope()
    RecipeModalBottomSheet(sheetContent = {
        RecipeDetailPreview(
            recipe = buildRecipeStates.recipe,
            provideExpandedValue = { buildRecipeStates.isExpanded },
            onExpandClick = onExpandValueChange,
            onConfirmClick = {
                coroutineScope.launch {
                    modalSheetState.hide()
                }
                onConfirmClick()
            }
        )
    }, modalSheetState = modalSheetState) {
        RecipeBuilder(
            coroutineScope,
            buildRecipeStates,
            modalSheetState,
            applyRecipe,
            onRecipeNameTextChange,
            onRecipeUriChange,
            onRecipeImageChange,
            onIngredientsAndMeasuresAdd,
            onIngredientChange,
            onMeasureChange,
            onIngredientsAndMeasuresRemove,
            onTextInstructionChange,
            onVideoInstructionChange
        )
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RecipeBuilder(
    coroutineScope: CoroutineScope,
    buildRecipeStates: BuildRecipeStates,
    modalSheetState: ModalBottomSheetState,
    applyRecipe: (Recipe) -> Unit,
    onIngredientTextChange: (String) -> Unit,
    onRecipeUriChange: (String) -> Unit,
    onRecipeImageChange: (Bitmap?) -> Unit,
    onIngredientsAndMeasuresAdd: (value: String) -> Unit,
    onIngredientChange: (index: Int, String) -> Unit,
    onMeasureChange: (index: Int, String) -> Unit,
    onIngredientsAndMeasuresRemove: (index: Int) -> Unit,
    onTextInstructionChange: (String) -> Unit,
    onVideoInstructionChange: (String) -> Unit
) {
    var tabsState by remember {
        mutableStateOf(0)
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        CustomTabs(
            titles = listOf("Create own recipe", "Generate recipe"),
            state = tabsState,
            onTabClick = { index ->
                tabsState = index
            })
        AddImageAndTitle(
            buildRecipeStates.recipeName,
            onTextChange = { onIngredientTextChange(it) },
            imageBitmap = buildRecipeStates.imageBitmap,
            onImageChange = onRecipeImageChange,
            getBitmapUri = { onRecipeUriChange(it.toString()) }
        )
        AddIngredients(
            buildRecipeStates.ingredients,
            buildRecipeStates.measures,
            onIngredientsAndMeasuresAdd,
            onIngredientChange,
            onMeasureChange,
            onIngredientsAndMeasuresRemove
        )
        AddInstructions(
            buildRecipeStates.textInstruction,
            buildRecipeStates.videoInstruction,
            onTextInstructionChange,
            onVideoInstructionChange
        )
        OutlinedButton(
            onClick = {
                val recipe = Recipe(
                    id = buildRecipeStates.recipeName.hashCode().toString(),
                    name = buildRecipeStates.recipeName,
                    imageUrl = buildRecipeStates.imageUri!!,
                    instruction = buildRecipeStates.textInstruction,
                    videoLink = buildRecipeStates.videoInstruction,
                    ingredients = buildRecipeStates.ingredients.zip(buildRecipeStates.measures).toMap(),
                )
                applyRecipe(recipe)
                coroutineScope.showModalSheetState(modalSheetState)
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Row {
                Icon(imageVector = Icons.Outlined.Edit, contentDescription = "")
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(text = "Create recipe", modifier = Modifier.align(Alignment.CenterVertically))
            }
        }
    }
}

@Composable
private fun AddImageAndTitle(
    text: String,
    imageBitmap: Bitmap?,
    getBitmapUri: (Uri) -> Unit,
    onTextChange: (String) -> Unit,
    onImageChange: (Bitmap) -> Unit
) {

    val context = LocalContext.current
    val takePictureFromFolder =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                getBitmapUri(uri)
                onImageChange(
                    MediaStore.Images.Media.getBitmap(
                        context.contentResolver,
                        uri
                    )
                )

            }
        }
    CustomSurface {
        Column {
            Image(
                painter = if (imageBitmap != null) rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageBitmap)
                        .crossfade(true)
                        .size(Size.ORIGINAL)
                        .build()
                ) else painterResource(id = R.drawable.select_image),
                contentDescription = "",
                modifier = Modifier
                    .size(
                        height = 400.dp,
                        width = LocalConfiguration.current.screenWidthDp.dp
                    )
                    .clickable {
                        takePictureFromFolder.launch("image/*")
                    }
            )

            BlankTextField(
                text = text,
                label = "Enter recipe name...",
                textStyle = MaterialTheme.typography.h6,
                onTextChange = onTextChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AddIngredients(
    ingredients: List<String>,
    measures: List<String>,
    onIngredientsAndMeasuresAdd: (value: String) -> Unit,
    onIngredientChange: (index: Int, String) -> Unit,
    onMeasureChange: (index: Int, String) -> Unit,
    onIngredientsAndMeasuresRemove: (index: Int) -> Unit,
) {
//    val ingredients = remember {
//        mutableStateListOf<String>()
//    }
//    val measures = remember {
//        mutableStateListOf<String>()
//    }
    CustomSurface(shape = CutCornerShape(topEnd = 16.dp, topStart = 16.dp)) {
        Column {
            TextButton(
                onClick = {
                    onIngredientsAndMeasuresAdd("")
                },
                modifier = Modifier
                    .wrapContentWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.onSurface)
            ) {
                Row {
                    Icon(imageVector = Icons.Outlined.AddBox, contentDescription = "")
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = "Add ingredient",
                        style = MaterialTheme.typography.button,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }

            for (index in ingredients.indices) {
                Row(modifier = Modifier.fillMaxWidth()) {

                    BlankTextField(
                        text = ingredients[index],
                        modifier = Modifier.weight(0.5f, false),
                        label = "Ingredient name...",
                        textStyle = MaterialTheme.typography.subtitle1,
                        onTextChange = { onIngredientChange(index, it) })
                    BlankTextField(
                        text = measures[index],
                        modifier = Modifier.weight(0.3f, false),
                        label = "Measure...",
                        textStyle = MaterialTheme.typography.subtitle1,
                        onTextChange = { onMeasureChange(index, it) })
                    Icon(
                        imageVector = Icons.Outlined.Backspace,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.CenterVertically)
                            .size(25.dp)
                            .clickable {
                                onIngredientsAndMeasuresRemove(index)
                            })
                }
            }
        }

    }

}

@Composable
private fun AddInstructions(
    textInstruction: String,
    videoInstruction: String,
    onTextInstructionChange: (String) -> Unit,
    onVideoInstructionChange: (String) -> Unit
) {
//    var textInstruction by remember {
//        mutableStateOf("")
//    }
//    var videoInstruction by remember {
//        mutableStateOf("")
//    }
    var tabs by remember {
        mutableStateOf(0)
    }
    CustomSurface(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Column(Modifier.padding(8.dp)) {
            CustomTabs(
                titles = listOf("Text Instruction", "Video Instruction (optional)"),
                state = tabs,
                onTabClick = { index ->
                    tabs = index
                })
            when (tabs) {
                InstructionTabsConstants.TEXT_INSTRUCTION -> BlankTextField(
                    text = textInstruction,
                    label = "Type instruction...",
                    textStyle = MaterialTheme.typography.subtitle1,
                    onTextChange = {
                        onTextInstructionChange(it)
                    },
                    modifier = Modifier.fillMaxSize(),
                    maxLines = Int.MAX_VALUE
                )
                InstructionTabsConstants.VIDEO_INSTRUCTION -> BlankTextField(
                    text = videoInstruction,
                    label = "Enter video link...",
                    textStyle = MaterialTheme.typography.subtitle1,
                    onTextChange = {
                        onVideoInstructionChange(it)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(
    showSystemUi = true
)
@Composable
fun BuildRecipeScreenPreview() {

//    BuildRecipeScreen()

}