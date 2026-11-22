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

@Serializable
data class PointDto(
    val point: String? = null,
    val evidence: String? = null
)

/**One job's full analysis, used for individual job matches as well as the full report */
@Serializable
data class JobReportSectionDto(
    @SerialName("job_id") val jobId: String? = null,
    @SerialName("job_title") val title: String? = null,
    @SerialName("company_name") val company: String? = null,
    val location: String? = null,
    val url: String? = null,
    @SerialName("job_description") val description: String? = null,
    val matches: List<PointDto> = emptyList(),
    @SerialName("non_matches") val nonMatches: List<PointDto> = emptyList(),
    @SerialName("matched_skills") val matchedSkills: List<String> = emptyList(),
    @SerialName("missing_skills") val missingSkills: List<String> = emptyList(),
    @SerialName("fit_score") val fitScore: Int? = null,
    @SerialName("fit_score_reason") val fitScoreReason: String? = null
)

@Serializable
data class JobDetailDto(
    val success: Boolean,
    @SerialName("job_id") val jobId: String,
    @SerialName("job_title") val title: String? = null,
    @SerialName("company_name") val company: String? = null,
    val location: String? = null,
    @SerialName("estimated_fit_score") val fitScore: Int? = null,
    @SerialName("discovered_at") val discoveredAt: String? = null,
    @SerialName("job_report_section") val jobReportSection: JobReportSectionDto? = null,
    @SerialName("matched_skills") val matchedSkills: List<String>    = emptyList(),
    @SerialName("missing_skills") val missingSkills: List<String> = emptyList(),
    @SerialName("original_link") val originalLink: String? = null
)