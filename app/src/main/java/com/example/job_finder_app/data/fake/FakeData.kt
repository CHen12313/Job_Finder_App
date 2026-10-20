package com.example.job_finder_app.data.fake

import com.example.job_finder_app.data.model.JobCard

val fakeMatches = listOf(
    JobCard(
        jobId = "1",
        jobTitle = "Developer",
        companyName = "Microsoft",
        location = "USA",
        estimatedFitScore = 78
    ),

    JobCard(
        jobId = "2",
        jobTitle = "Software Developer",
        companyName = "Apple",
        location = "USA",
        estimatedFitScore = 56
    ),

    JobCard(
        jobId = "3",
        jobTitle = "Data scientist",
        companyName = "Microsoft",
        location = "Ireland",
        estimatedFitScore = 65
    )
)