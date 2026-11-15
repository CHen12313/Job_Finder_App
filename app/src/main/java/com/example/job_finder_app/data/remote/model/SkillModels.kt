package com.example.job_finder_app.data.remote.model

import kotlinx.serialization.Serializable

// The server sends skills as a JSON object whose KEYS are skill names and
// whose values are a 2-number array [missing, matched]:
//     "skills": { "AWS": [7, 1], "Python": [0, 12] }
// A JSON object with unknown keys maps to a Kotlin Map, and a JSON array of
// numbers maps to List<Int>. No @SerialName needed: the names already match.

@Serializable
data class SkillsTrendResponse(
    val success: Boolean,
    val skills: Map<String, List<Int>> = emptyMap()
)

