package com.example.job_finder_app.data.model

data class Report (
    val runId : Int,
    val reportDate : String,
    val query : String?,
    val location : String?,
    val jobCount : Int,
    val avgScore : Int?,
    val topScore : Int?,
    val fileName : String
)