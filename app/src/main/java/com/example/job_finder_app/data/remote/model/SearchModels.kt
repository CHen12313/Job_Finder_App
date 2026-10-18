package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** The user's saved search parameters (`user_params`). See `upload_parameters` in Django_Server_README.md. */
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
