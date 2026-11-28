package com.designlife.setwork

import android.app.Application
import android.util.Log
import com.designlife.setwork.common.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SetworkApplication : Application(){
    private val TAG = SetworkApplication::class.java.simpleName
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            throwable.printStackTrace()
            Log.i(TAG, "onCreate: SETWORK CRASH :: Crash happened on thread ${thread.name} ::  ${throwable.cause?.message}")

        }
        startKoin {
            androidLogger()
            androidContext(this@SetworkApplication)
            modules(appModule)
        }

    }
}