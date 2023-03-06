package com.pi.recipeapp.koin

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pi.recipeapp.repository.RecipeRepository
import com.pi.recipeapp.repository.RecipeRepositoryImpl
import com.pi.recipeapp.retrofit.RetrofitClient
import com.pi.recipeapp.room.RecipesDatabase
import com.pi.recipeapp.ui.screens.build.BuildRecipeViewModel
import com.pi.recipeapp.ui.screens.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        Room.databaseBuilder(androidContext(), RecipesDatabase::class.java, "Recipes Database").fallbackToDestructiveMigration()
            .build()
    }
    single {
        get<RecipesDatabase>()
            .also { CoroutineScope(Dispatchers.IO).launch {  it.clearAllTables() }}
            .recipesDao()
    }
}
val repositoryModule = module {
    single<RecipeRepository> { RecipeRepositoryImpl(get(),get()) }
}
val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { BuildRecipeViewModel() }
}