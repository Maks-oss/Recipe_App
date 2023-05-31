package com.pi.recipeapp.koin

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class RecipeApplication : Application() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Koin Android logger
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            //inject Android context
            androidContext(this@RecipeApplication)
            modules(
                retrofitModule,
                repositoryModule,
                firebaseModule,
                authModule,
                viewModelModule
            )
        }

    }
}