package com.pi.recipeapp.koin

import com.pi.recipeapp.retrofit.RetrofitClient
import com.pi.recipeapp.ui.screens.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val retrofitModule = module {
    single {
        RetrofitClient.provideRetrofit()
    }
    single { RetrofitClient.provideRecipesService(get()) }
}
val viewModelModule = module {
    viewModel { MainViewModel(get()) }
}