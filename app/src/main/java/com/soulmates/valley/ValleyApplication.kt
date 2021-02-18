package com.soulmates.valley

import android.app.Application
import com.soulmates.valley.data.local.pref.SharedPreferencesManager
import com.soulmates.valley.di.*
import io.socket.client.IO
import io.socket.client.Socket
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import java.net.URISyntaxException

class ValleyApplication : Application() {

    companion object {
        lateinit var globalApplication: ValleyApplication
        lateinit var instance: ValleyApplication
        lateinit var prefManager: SharedPreferencesManager

        fun getGlobalApplicationContext(): ValleyApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        globalApplication = this
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ValleyApplication)
            modules(listOf(localModule, serviceModule, repositoryModule, viewModelModule, dataSourceModule))
        }

        Timber.plant(Timber.DebugTree())
        prefManager = SharedPreferencesManager(applicationContext)
    }
}