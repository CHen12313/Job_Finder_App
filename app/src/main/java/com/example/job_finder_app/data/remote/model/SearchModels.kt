package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SearchParameters(
    val region: String? = null,
    val query: String? = null,
    val where: String? = null,
    @SerialName("extra_parameters") val extraParameters: JsonObject? = null
)

/** Company filter rules (`user_rules`). */
@Serializable
data class FilterRules(
    @SerialName("filter_mode") val filterMode: String = "off",
    @SerialName("blacklist_companies") val blacklistCompanies: List<String> = emptyList(),
    @SerialName("whitelist_companies") val whitelistCompanies: List<String> = emptyList()
)

@Serializable
data class UploadParametersRequest(
    val region: String?,
    val query: String?,
    val where: String?,
    @SerialName("extra_parameters") val extraParameters: JsonObject? = null,
    @SerialName("filter_mode") val filterMode: String,
    @SerialName("blacklist_companies") val blacklistCompanies: List<String>,
    @SerialName("whitelist_companies") val whitelistCompanies: List<String>
    )

@Serializable
data class UploadParametersResponse(
    val success: Boolean,
    val error: String? = null,
    val parameters: SearchParameters? = null,
    val rules: FilterRules? = null
)


@Serializable
data class GetSearchParametersResponse(
    val success: Boolean,
    @SerialName("search_parameters") val searchParameters: SearchParameters? = null,
    @SerialName("filter_rule") val filterRules: FilterRules? = null,
    @SerialName("subscription_type") val subscriptionType: String = "free"
)