package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.Serializable

/**
 * Error body returned by the server. Routes disagree on the field name:
 * some use "error", some use "message", and login has no "success" key at all.
 * See "Error shapes" in Django_Server_README.md.
 */
@Serializable
data class ApiError(
    val success: Boolean? = null,
    val error: String? = null,
    val message: String? = null
) {
    val text: String
        get() = error ?: message ?: "unknown error"
}
