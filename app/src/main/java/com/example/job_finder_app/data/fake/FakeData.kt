package com.example.job_finder_app.data.fake

import com.example.job_finder_app.data.model.JobCard
import com.example.job_finder_app.data.model.Skill

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

val fakeSkills = listOf(
    Skill(
        name = "Python",
        missing = 0,
        matched = 4
    ),
    Skill(
        name = "SQL",
        missing = 0,
        matched = 3
    ),
    Skill(
        name = "Java",
        missing = 6,
        matched = 0
    ),
    Skill(
        name = "HTML",
        missing = 2,
        matched = 0
    )
)