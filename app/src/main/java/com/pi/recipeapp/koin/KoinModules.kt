package com.pi.recipeapp.koin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pi.recipeapp.BuildConfig
import com.pi.recipeapp.firebase.auth.GoogleAuth
import com.pi.recipeapp.firebase.auth.InAppAuth
import com.pi.recipeapp.repository.RecipeGeneratorRepository
import com.pi.recipeapp.repository.RecipeRepository
import com.pi.recipeapp.repository.RecipeRepositoryImpl
import com.pi.recipeapp.retrofit.RetrofitClient
import com.pi.recipeapp.ui.screens.build.BuildRecipeViewModel
import com.pi.recipeapp.ui.screens.imagesearch.ImageSearchViewModel
import com.pi.recipeapp.ui.screens.main.TextSearchViewModel
import com.pi.recipeapp.firebase.utils.CloudStorageUtil
import com.pi.recipeapp.firebase.utils.FirebaseUtil
import com.pi.recipeapp.ml.LiteModelAiyVisionClassifierFoodV11
import com.pi.recipeapp.ui.screens.detail.RecipeDetailViewModel
import com.pi.recipeapp.ui.screens.saved.SavedRecipesViewModel
import com.theokanning.openai.service.OpenAiService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.time.Duration

val retrofitModule = module {
    single {
        RetrofitClient.provideRetrofit()
    }
    single { RetrofitClient.provideRecipesService(get()) }
}

val authModule = module {
    single { GoogleAuth(androidContext()) }
    single { InAppAuth(get()) }
}
@RequiresApi(Build.VERSION_CODES.O)
val repositoryModule = module {
    single<RecipeRepository> { RecipeRepositoryImpl(get(), get(), LiteModelAiyVisionClassifierFoodV11.newInstance(androidContext())) }
    single { RecipeGeneratorRepository(OpenAiService(BuildConfig.openApiKey, Duration.ofSeconds(15))) }
}
val firebaseModule = module {
    single { Firebase.database.reference }
    single { Firebase.storage.reference }
    single { CloudStorageUtil(get()) }
    single { FirebaseUtil(get(), get()) }
}
val viewModelModule = module {
    viewModel { TextSearchViewModel(get()) }
    viewModel { ImageSearchViewModel(get()) }
    viewModel { RecipeDetailViewModel(get()) }
    viewModel { SavedRecipesViewModel(get()) }
    viewModel { BuildRecipeViewModel(get(), get()) }
}