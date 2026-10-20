package com.example.job_finder_app.data.remote

import com.example.job_finder_app.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.security.KeyStore

/**
 * Builds the networking stack: Json → OkHttpClient (cookies, CSRF, logging) → Retrofit → JobFinderApi.
 * One instance of each should exist for the whole app (created once in step 5).
 */
object NetworkModule {
    val json = Json{
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }

    fun createOkHttpClient(cookieJar: PersistentCookieJar): OkHttpClient {
        val baseUrl = BuildConfig.BASE_URL.toHttpUrl()

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        } //I need to see what's going on inside the code while debugging, but in prod, there is no need to log

        return OkHttpClient.Builder().cookieJar(cookieJar).addInterceptor(CsrfInterceptor(cookieJar, baseUrl)).
        addInterceptor(logging).followRedirects(false).build()
    }

    fun createApi(client: OkHttpClient): JobFinderApi {
        return Retrofit.Builder().baseUrl((BuildConfig.BASE_URL)).client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build().create(JobFinderApi::class.java)
    //JobFinderApi will be the API interface I interact with, Retrofit will translate kotlin API calls to HTTP requests for OkHttp
    }
}
