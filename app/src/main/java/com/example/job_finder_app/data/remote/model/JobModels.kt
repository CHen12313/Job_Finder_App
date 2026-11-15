package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//Dto as-in Data Transfer Object, frontend has its own data classes, we need this class to get the raw data from server,
// which can be null
@Serializable
data class JobDto(
    @SerialName("job_id") val jobId: String,
    @SerialName("job_title") val title: String? = null,
    @SerialName("company_name") val company: String? = null,
    val location: String? = null,
    @SerialName("estimated_fit_score") val fitScore: Int? = null,
    @SerialName("discovered_at") val discoveredAt: String? = null
)

@Serializable
data class JobCardPage(
    val success: Boolean,
    val jobs: List<JobDto> = emptyList(),
    @SerialName("next_cursor") val nextCursor: String? = null
)
