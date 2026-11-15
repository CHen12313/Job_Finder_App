package com.example.job_finder_app.data.remote

import retrofit2.Response
class NotLoggedInException : Exception("Not logged in")
class ApiException(val code: Int, message: String) : Exception(message)

fun <T> Response<T>.bodyOrThrow(): T {
    if (code() == 302 || code() == 401) throw NotLoggedInException()
    val body = body()
    if (isSuccessful && body != null) return body
    throw ApiException(code(), "Server error ${code()}")
}