package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** The user's saved search parameters (`user_params`). See `upload_parameters` in Django_Server_README.md. */
@Serializable
data class SearchParameters(
    // TODO: region, query, where, extra_parameters (free-form object → JsonObject)
)

/** Company filter rules (`user_rules`). */
@Serializable
data class FilterRules(
    // TODO: filter_mode ("off" | "blacklist" | "whitelist"), blacklist_companies, whitelist_companies
)
