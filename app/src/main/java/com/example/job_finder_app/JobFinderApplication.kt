package com.example.job_finder_app

import android.app.Application

/**
 * Runs once when the app process starts, before any screen.
 * Registered in AndroidManifest.xml with android:name=".JobFinderApplication".
 */
class JobFinderApplication : Application() {
    lateinit var container: AppContainer private set

    override fun onCreate(){
        super.onCreate()
        container = AppContainer(this)
    }
}
