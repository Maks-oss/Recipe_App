package com.pi.recipeapp.ui.screens.login

import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
fun LoginScreen(navigateToMainScreen: () -> Unit, navigateToRegistrationScreen: () -> Unit) {
    Column(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.recipe_corner),
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
        Row(
            Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = navigateToMainScreen,
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colors.primary)
            ) {
                Text(text = "Sign In")
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Button(
                onClick = navigateToRegistrationScreen,
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colors.primary)
            ) {
                Text(text = "Sign Up")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen({},{})
}