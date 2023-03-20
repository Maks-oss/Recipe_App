package com.pi.recipeapp.koin

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pi.recipeapp.firebase.database.RealtimeDatabaseUtil
import com.pi.recipeapp.firebase.storage.CloudStorageUtil
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class RecipeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        RealtimeDatabaseUtil.databaseReference = Firebase.database.reference
        CloudStorageUtil.storageReference = Firebase.storage.reference

        startKoin {
            // Koin Android logger
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            //inject Android context
            androidContext(this@RecipeApplication)
            modules(retrofitModule, repositoryModule, databaseModule,viewModelModule)
        }

    }
}