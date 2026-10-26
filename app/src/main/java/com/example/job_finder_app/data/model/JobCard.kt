package com.example.job_finder_app.data.model

data class JobCard(
    val jobId: String,
    val jobTitle: String,
    val companyName: String,
    val location: String,
    val estimatedFitScore: Int
)