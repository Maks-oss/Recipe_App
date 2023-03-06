package com.pi.recipeapp.ui.navigation

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun NavController.navigateThroughDrawer(route: String, coroutineScope: CoroutineScope, scaffoldState: ScaffoldState) {
    navigate(route)
    coroutineScope.launch {
        scaffoldState.drawerState.close()
    }
}

fun NavController.navigateWithPopUp(route: String, popUpRoute: String){
    navigate(route) {
        popUpTo(popUpRoute) {
            inclusive = true
        }
    }
}