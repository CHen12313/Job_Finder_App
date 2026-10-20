package com.example.job_finder_app.data.remote

import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class CsrfInterceptor(
    private val cookieJar: CookieJar,
    baseUrl: HttpUrl,
) : Interceptor {
        private val origin = baseUrl.toString().trimEnd('/')

    override fun intercept(chain: Interceptor.Chain): Response {
        //intercepts the request and add CSRF token from the cookieJar, the server requires that every unsafe request
        // to carry a valid CSRF token.
        val request = chain.request()
        if (request.method in SAFE_METHODS) return chain.proceed(request)

        val csrfToken = cookieJar.loadForRequest(request.url).firstOrNull {it.name == "csrftoken"}?.value

        val newRequest = request.newBuilder().header("Origin", origin)
            .apply { if (csrfToken != null) header("X-CSRFToken", csrfToken)}.build()

        return chain.proceed(newRequest)

    }

     private companion object {
         val SAFE_METHODS = setOf("GET", "HEAD", "OPTIONS", "TRACE")
     }
}
