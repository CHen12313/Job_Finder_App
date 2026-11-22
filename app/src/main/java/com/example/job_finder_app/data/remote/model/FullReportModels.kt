package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImprovementDto(
    val title: String? = null,
    val detail: String? = null
)

@Serializable
data class FullReportDto(
    val jobs: List<JobReportSectionDto>? = emptyList(),
    val improvements: List<ImprovementDto>? = emptyList(),
)

@Serializable
data class JsonReportResponse(
    val success: Boolean,
    @SerialName("run_id") val runId: Long,
    @SerialName("report_date") val reportDate: String,
    val query: String? = null,
    val location: String? = null,
    @SerialName("job_count") val jobCount: Int,
    @SerialName("avg_score") val avgScore: Int? = null,
    @SerialName("top_score") val topScore: Int? = null,
    val report: FullReportDto? = null
)