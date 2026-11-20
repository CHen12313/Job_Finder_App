package com.example.job_finder_app.data.remote

import com.example.job_finder_app.data.remote.model.ApiError
import kotlinx.serialization.decodeFromString
import retrofit2.Response
class NotLoggedInException : Exception("Not logged in")
class ApiException(val code: Int, message: String) : Exception(message)

fun <T> Response<T>.bodyOrThrow(): T {
    if (code() == 302 || code() == 401) throw NotLoggedInException()
    val body = body()
    if (isSuccessful && body != null) return body
    throw ApiException(code(), "Server error ${code()}")
}

fun Response<*>.errorText(): String {
    val rawErrorText: String? = errorBody()?.string()
    if (rawErrorText.isNullOrBlank()) return "Server Error ${code()}"
    return runCatching { NetworkModule.json.decodeFromString<ApiError>(rawErrorText) }.getOrNull()?.text?: rawErrorText
}
