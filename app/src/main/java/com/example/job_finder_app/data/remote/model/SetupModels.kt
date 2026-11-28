package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class SetupStatusDto(
    @SerialName("user_cv_set") val cvSet: Boolean = false,
    @SerialName("user_search_set") val searchSet: Boolean = false,
    @SerialName("first_report_generated") val firstReportGenerated: Boolean = false,
    @SerialName("user_setup_complete") val setupComplete: Boolean? = null
)

@Serializable
data class SetupStatusResponse(
    val success: Boolean,
    @SerialName("user_setup_status") val status: SetupStatusDto
)