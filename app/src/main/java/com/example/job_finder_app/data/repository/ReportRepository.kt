package com.example.job_finder_app.data.repository

import com.example.job_finder_app.data.remote.ApiException
import com.example.job_finder_app.data.remote.JobFinderApi
import com.example.job_finder_app.data.remote.bodyOrThrow
import com.example.job_finder_app.data.remote.errorText
import com.example.job_finder_app.data.remote.model.JsonReportResponse


class ReportRepository(private val api: JobFinderApi) {
    suspend fun getFullReport(runId: Long): Result<JsonReportResponse> = runCatching{
        val response = api.getJsonReport()
        if (!response.isSuccessful) throw ApiException(response.code(), response.errorText())
        val body = response.bodyOrThrow()
        if (!body.success) throw ApiException(response.code(), body.error?: "Could not get full report")
        body
    }
}
