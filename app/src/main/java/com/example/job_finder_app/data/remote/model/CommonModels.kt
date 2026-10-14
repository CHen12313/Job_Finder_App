package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.Serializable

/** Generic `{"success": ..., "message"?: ..., "error"?: ...}` response (login, logout, send code, save job…). */
@Serializable
data class SuccessResponse(
    // TODO: success (required), message and error (optional)
)
