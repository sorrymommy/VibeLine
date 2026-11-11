package com.vibeline.app

import android.app.Application
import android.content.Context
import com.vibeline.app.data.AppDatabase
import com.vibeline.app.data.AppLock
import com.vibeline.app.data.AppPrefs
import com.vibeline.app.data.EntryRepository
import com.vibeline.app.sync.SyncScheduler

class VibeApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        if (container.prefs.syncEnabled) SyncScheduler.enablePeriodic(this, container.prefs)
    }
}

class AppContainer(context: Context) {
    val prefs = AppPrefs(context)
    val lock = AppLock(context)
    val repository = EntryRepository(context, AppDatabase.build(context).entryDao(), prefs)
}

val Context.container: AppContainer get() = (applicationContext as VibeApp).container
