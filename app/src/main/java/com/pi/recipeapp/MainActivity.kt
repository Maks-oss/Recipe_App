package com.pi.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.pi.recipeapp.ui.screens.MainScreen
import com.pi.recipeapp.ui.screens.MainViewModel
import com.pi.recipeapp.ui.theme.RecipeAppTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = getViewModel<MainViewModel>()
        setContent {
            RecipeAppTheme {
                MainScreen(viewModel)
            }
        }
    }
}
