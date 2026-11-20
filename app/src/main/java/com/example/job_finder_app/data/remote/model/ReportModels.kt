package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportDto(
    @SerialName("run_id") val runId: Long,
    @SerialName("report_date") val reportDate: String,
    @SerialName("query") val query: String? = null,
    @SerialName("location") val location: String? = null,
    @SerialName("job_count") val jobCount: Int,
    @SerialName("avg_score") val avgScore: Int? = null,
    @SerialName("top_score") val topScore: Int? = null,
    @SerialName("filename") val filename: String,
    @SerialName("download_url") val downloadUrl: String? = null
)

@Serializable
data class ReportListResponse(
    val success: Boolean,
    val reports: List<ReportDto> = emptyList()
)