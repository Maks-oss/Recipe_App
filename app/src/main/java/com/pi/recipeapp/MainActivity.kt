package com.pi.recipeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.material.Button
import androidx.compose.material.Text
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pi.recipeapp.firebase.authorization.GoogleAuth
import com.pi.recipeapp.firebase.database.RealtimeDatabaseUtil
import com.pi.recipeapp.ui.screens.imagesearch.RecipeImageSearchScreen
import com.pi.recipeapp.ui.screens.main.MainScreen
import com.pi.recipeapp.ui.screens.main.MainViewModel
import com.pi.recipeapp.ui.theme.RecipeAppTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val googleAuth = GoogleAuth(this)
        val currentUser = Firebase.auth.currentUser
        setContent {
            RecipeAppTheme {
               AppNavigator(googleAuth)
            }
        }
    }
}
