package com.pi.recipeapp.koin

import androidx.room.Room
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pi.recipeapp.auth.GoogleAuth
import com.pi.recipeapp.auth.InAppAuth
import com.pi.recipeapp.repository.RecipeGeneratorRepository
import com.pi.recipeapp.repository.RecipeRepository
import com.pi.recipeapp.repository.RecipeRepositoryImpl
import com.pi.recipeapp.retrofit.RetrofitClient
import com.pi.recipeapp.room.RecipesDatabase
import com.pi.recipeapp.ui.screens.build.BuildRecipeViewModel
import com.pi.recipeapp.ui.screens.main.MainViewModel
import com.pi.recipeapp.utils.CloudStorageUtil
import com.theokanning.openai.service.OpenAiService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val retrofitModule = module {
    single {
        RetrofitClient.provideRetrofit()
    }
    single { RetrofitClient.provideRecipesService(get()) }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), RecipesDatabase::class.java, "Recipes Database")
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        get<RecipesDatabase>()
//            .also { CoroutineScope(Dispatchers.IO).launch { it.clearAllTables() } }
            .recipesDao()
    }
}
val authModule = module {
    single { GoogleAuth(androidContext()) }
    single { InAppAuth(get()) }
}
val repositoryModule = module {
    single<RecipeRepository> { RecipeRepositoryImpl(get(), get(), get(), get()) }
    single<RecipeGeneratorRepository> { RecipeGeneratorRepository(OpenAiService("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")) }
}
val firebaseModule = module {
    single { Firebase.database.reference }
    single { Firebase.storage.reference }
    single { CloudStorageUtil(get()) }
}
val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { BuildRecipeViewModel(get()) }
}