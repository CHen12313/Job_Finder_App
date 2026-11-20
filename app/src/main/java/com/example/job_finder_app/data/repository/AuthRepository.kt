package com.example.job_finder_app.data.repository

import android.os.Build
import com.example.job_finder_app.BuildConfig
import com.example.job_finder_app.data.remote.ApiException
import com.example.job_finder_app.data.remote.JobFinderApi
import com.example.job_finder_app.data.remote.bodyOrThrow
import com.example.job_finder_app.data.remote.errorText
import com.example.job_finder_app.data.remote.model.LoginRequest
import com.example.job_finder_app.data.remote.model.User
import javax.security.auth.login.LoginException


class AuthRepository(private val api: JobFinderApi) {
    suspend fun login(username: String, password: String): Result<User> = runCatching {
        api.authUser() /** Get the csrf token */
        val response = api.login(
                LoginRequest(
                username = username.trim(),
                password = password,
                remember = true,
                turnstileToken = BuildConfig.TURNSTILE_TOKEN
            )
        )

        if (! response.isSuccessful) throw ApiException(response.code(), message = response.errorText())

        val user = api.authUser().bodyOrThrow().user ?: error("Logged in, but the server returned no user")

        user
    }
}
