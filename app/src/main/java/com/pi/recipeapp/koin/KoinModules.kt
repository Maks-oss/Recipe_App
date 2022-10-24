package com.pi.recipeapp.koin

import com.pi.recipeapp.repository.RecipeRepository
import com.pi.recipeapp.repository.RecipeRepositoryImpl
import com.pi.recipeapp.retrofit.RetrofitClient
import com.pi.recipeapp.ui.screens.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module

val retrofitModule = module {
    single {
        RetrofitClient.provideRetrofit()
    }
    single { RetrofitClient.provideRecipesService(get()) }
}
val repositoryModule = module {
    single<RecipeRepository> { RecipeRepositoryImpl(get()) }
}
val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}