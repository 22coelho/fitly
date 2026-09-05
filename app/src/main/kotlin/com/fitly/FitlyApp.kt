package com.fitly

import android.app.Application
import com.fitly.di.dataModule
import com.fitly.di.databaseModule
import com.fitly.di.photoModule
import com.fitly.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FitlyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FitlyApp)
            modules(databaseModule, dataModule, photoModule, presentationModule)
        }
    }
}
