package com.pi.recipeapp.ui.screens.login

import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.pi.recipeapp.R
import com.pi.recipeapp.ui.utils.DisplayTextFieldError
import com.pi.recipeapp.utils.AuthUtil

@Composable
fun RegistrationScreen(register: (email: String, password: String, imageUri: Uri?) -> Unit) {
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var repeatPassword by remember {
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
    var isRepeatPasswordError by remember {
        mutableStateOf(false)
    }
    var userImageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val takePictureFromFolder =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                userImageUri = uri
            }
        }
    val userImage = ImageRequest.Builder(LocalContext.current)
        .data(userImageUri)
        .crossfade(true)
        .size(Size.ORIGINAL)
        .build()
    Column(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = if (userImageUri == null) painterResource(id = R.drawable.select_image) else rememberAsyncImagePainter(
                model = userImage
            ),
            contentDescription = "",
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .clickable {
                    takePictureFromFolder.launch("image/*")
                }
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            isError = isEmailError,
            value = email, onValueChange = { email = it }, shape = CircleShape, modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "") },
            trailingIcon = { Icon(imageVector = Icons.Filled.Clear, contentDescription = "") },
            label = { Text(text = "Enter email...") }
        )
        DisplayTextFieldError(isError = isEmailError, errorMessage = stringResource(id = R.string.email_error))
        OutlinedTextField(
            isError = isPasswordError,
            value = password, onValueChange = { password = it}, shape = CircleShape, modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(imageVector = Icons.Default.Password, contentDescription = "") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = ""
                )
            },
            label = { Text(text = "Enter password...") }
        )
        DisplayTextFieldError(isError = isPasswordError, errorMessage = stringResource(id = R.string.password_error))
        OutlinedTextField(
            isError = isRepeatPasswordError,
            value = repeatPassword, onValueChange = { repeatPassword = it }, shape = CircleShape, modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(imageVector = Icons.Default.Password, contentDescription = "") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = ""
                )
            },
            label = { Text(text = "Repeat password...") }
        )
        DisplayTextFieldError(isError = isRepeatPasswordError, errorMessage = stringResource(id = R.string.repeat_password_error))
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedButton(
            onClick = {
                isPasswordError = AuthUtil.isPasswordInvalid(password)
                isEmailError = AuthUtil.isEmailInvalid(email)
                isRepeatPasswordError = AuthUtil.isRepeatPasswordInvalid(repeatPassword, password)
                if (!isPasswordError && !isEmailError && !isRepeatPasswordError) {
                    register(email, password, userImageUri)
                }
            },
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
    RegistrationScreen { _, _, _ -> }
}