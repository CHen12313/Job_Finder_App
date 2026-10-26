package com.example.job_finder_app

import android.content.Context
import com.example.job_finder_app.data.remote.JobFinderApi
import com.example.job_finder_app.data.remote.NetworkModule
import com.example.job_finder_app.data.remote.PersistentCookieJar
import okhttp3.OkHttpClient

/**
 * Holds the app-wide objects that must exist exactly once (one cookie jar, one HTTP client, one API).
 * Created by [JobFinderApplication] when the app process starts; everything else gets them from here.
 *
 * This is "manual dependency injection": instead of each screen building its own client,
 * they all ask the container for the shared one.
 */
class AppContainer(context: Context) {

    val cookieJar = PersistentCookieJar(context)
    val okHttpClient: OkHttpClient = NetworkModule.createOkHttpClient((cookieJar))
    val api: JobFinderApi = NetworkModule.createApi(okHttpClient)
}
