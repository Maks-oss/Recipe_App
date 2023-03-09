package com.pi.recipeapp.ui.screens.login

import android.util.Patterns
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pi.recipeapp.R
import com.pi.recipeapp.ui.utils.DisplayTextFieldError
import com.pi.recipeapp.utils.AuthUtil

@Composable
fun LoginScreen(
    signInGoogle: () -> Unit,
    signInInApp: (email: String, password: String) -> Unit,
    navigateToRegistrationScreen: () -> Unit
) {
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    var isEmailError by remember {
        mutableStateOf(false)
    }
    var isPasswordError by remember {
        mutableStateOf(false)
    }
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
            value = email,
            isError = isEmailError,
            onValueChange = { email = it },
            shape = CircleShape,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "") },
            trailingIcon = { Icon(imageVector = Icons.Filled.Clear, contentDescription = "", modifier = Modifier.clickable {
                email = ""
            }) },
            label = { Text(text = "Enter email...") }
        )
        DisplayTextFieldError(isError = isEmailError, errorMessage = stringResource(R.string.email_error))

        OutlinedTextField(
            value = password,
            isError = isPasswordError,
            onValueChange = { password = it },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = CircleShape,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(imageVector = Icons.Default.Password, contentDescription = "") },
            trailingIcon = {
                Icon(
                    imageVector = if (!passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "",
                    modifier = Modifier.clickable {
                        passwordVisible = !passwordVisible
                    }
                )
            },
            label = { Text(text = "Enter password...") }
        )
        DisplayTextFieldError(isError = isPasswordError, errorMessage = stringResource(R.string.password_error))
        Row(
            Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    isPasswordError = AuthUtil.isPasswordInvalid(password)
                    isEmailError = AuthUtil.isEmailInvalid(email)
                    if (!isPasswordError && !isEmailError) {
                        signInInApp(email, password)
                    }

                },
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
        OutlinedButton(
            onClick = signInGoogle,
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colors.primary)
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(text = "Sign in with Google")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen({}, { _, _ -> }, {})
}