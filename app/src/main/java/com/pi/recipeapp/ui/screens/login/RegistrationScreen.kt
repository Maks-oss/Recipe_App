package com.pi.recipeapp.ui.screens.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pi.recipeapp.R

@Composable
fun RegistrationScreen() {
    Column(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.select_image),
            contentDescription = "",
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            value = "", onValueChange = {}, shape = CircleShape, modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "") },
            trailingIcon = { Icon(imageVector = Icons.Filled.Clear, contentDescription = "") },
            label = { Text(text = "Enter email...") }
        )

        OutlinedTextField(
            value = "", onValueChange = {}, shape = CircleShape, modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(imageVector = Icons.Default.Password, contentDescription = "") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = ""
                )
            },
            label = { Text(text = "Enter password...") }
        )
        OutlinedTextField(
            value = "", onValueChange = {}, shape = CircleShape, modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(imageVector = Icons.Default.Password, contentDescription = "") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = ""
                )
            },
            label = { Text(text = "Repeat password...") }
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedButton(
            onClick = { },
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Text(text = "Register")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen()
}