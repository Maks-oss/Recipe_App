package com.pi.recipeapp.ui.utils

import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout
import com.google.firebase.auth.FirebaseUser
import com.pi.recipeapp.data.domain.Recipe
import com.pi.recipeapp.firebase.database.RealtimeDatabaseUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun FirebaseUser?.addSavedRecipesListener(onDataChange: (List<Recipe>?) -> Unit) {
    if (this != null) {
        RealtimeDatabaseUtil.addUserRecipesListener(this.uid, onDataChange)
    }
}
