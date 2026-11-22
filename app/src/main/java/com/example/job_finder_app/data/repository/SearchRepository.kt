package com.example.job_finder_app.data.repository

import com.example.job_finder_app.data.remote.ApiException
import com.example.job_finder_app.data.remote.JobFinderApi
import com.example.job_finder_app.data.remote.bodyOrThrow
import com.example.job_finder_app.data.remote.errorText
import com.example.job_finder_app.data.remote.model.GetSearchParametersResponse
import com.example.job_finder_app.data.remote.model.UploadParametersRequest
import com.example.job_finder_app.data.remote.model.UploadParametersResponse
import kotlinx.serialization.json.JsonObject

class SearchRepository(private val api: JobFinderApi) {

    suspend fun getSearchSettings(): Result<GetSearchParametersResponse> = runCatching {
        api.getSearchParameters().bodyOrThrow()
    }

    suspend fun saveSearchSettings(
        region: List<String>,
        query: List<String>,
        where: List<String>,
        extraParameters: JsonObject?,
        filterMode: String,
        blacklistCompanies: List<String>,
        whitelistCompanies: List<String>
    ): Result<UploadParametersResponse> = runCatching{
        val request = UploadParametersRequest(region, query, where, extraParameters,
                                                filterMode, blacklistCompanies, whitelistCompanies)
        val response = api.uploadParameters(request)
        if (!response.isSuccessful) throw ApiException(response.code(), response.errorText())
        val body = response.bodyOrThrow()
        if (!body.success) throw ApiException(response.code(), body.error?: "Could not save settings")
        body
    }
}
