package com.pi.recipeapp.ui.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
            }, overflow = TextOverflow.Ellipsis
        )
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
fun CustomSurface(
    shape: Shape = CutCornerShape(16.dp),
    borderStroke: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    Surface(
        shape = shape, modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(), elevation = 1.dp,
        border = borderStroke
    ) {
        content()
    }
}

@Composable
fun CustomSurface(
    modifier: Modifier = Modifier,
    borderStroke: BorderStroke? = null,
    shape: Shape = CutCornerShape(16.dp),
    content: @Composable () -> Unit
) {
    Surface(
        shape = shape, modifier = modifier, elevation = 1.dp, border = borderStroke
    ) {
        content()
    }
}

@Composable
fun CustomTabs(titles: List<String>, state: Int, onTabClick: (index: Int) -> Unit) {
//    val titles = listOf("Text instruction", "Video instruction")
    TabRow(
        selectedTabIndex = state,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.primary
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = state == index,
                onClick = {
                    onTabClick(index)
                },
                text = {
                    Text(
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            )
        }
    }
}

@Composable
fun BlankTextField(
    text: String,
    label: String,
    textStyle: TextStyle,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 1
) {
    TextField(
        value = text,
        onValueChange = { onTextChange(it) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = MaterialTheme.colors.surface,
            focusedBorderColor = MaterialTheme.colors.surface
        ),
        modifier = modifier,
        maxLines = maxLines,
        textStyle = textStyle,
        label = {
            Text(
                text = label,
            )
        },
    )
}

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    fullText: String,
    linkText: List<String>,
    linkTextColor: Color = Color.Blue,
    linkTextFontWeight: FontWeight = FontWeight.Medium,
    linkTextDecoration: TextDecoration = TextDecoration.Underline,
    hyperlinks: List<String> = emptyList(),
    fontSize: TextUnit = TextUnit.Unspecified
) {
    val annotatedString = buildAnnotatedString {
        append(fullText)
        linkText.forEachIndexed { index, link ->
            val startIndex = fullText.indexOf(link)
            val endIndex = startIndex + link.length
            addStyle(
                style = SpanStyle(
                    color = linkTextColor,
                    fontSize = fontSize,
                    fontWeight = linkTextFontWeight,
                    textDecoration = linkTextDecoration
                ),
                start = startIndex,
                end = endIndex
            )
            addStringAnnotation(
                tag = "URL",
                annotation = hyperlinks[index],
                start = startIndex,
                end = endIndex
            )
        }
        addStyle(
            style = SpanStyle(
                fontSize = fontSize
            ),
            start = 0,
            end = fullText.length
        )
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(
        modifier = modifier,
        text = annotatedString,
        onClick = {
            annotatedString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}

@Composable
fun DisplayTextFieldError(isError: Boolean, errorMessage: String) {
    if (isError) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.caption,
            color = Color.Red
        )
    }
}