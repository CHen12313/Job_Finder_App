package com.example.job_finder_app.data.fake

import com.example.job_finder_app.data.model.JobCard
import com.example.job_finder_app.data.model.Skill
import com.example.job_finder_app.data.model.Report
import com.example.job_finder_app.data.model.SetupStatus
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

val fakeReports = listOf(
    Report(
        runId = 1,
        reportDate = "2026-12-01",
        query = "Software Engineer",
        location = "USA",
        jobCount = 1,
        avgScore = 67,
        topScore = 67,
        fileName = "Job_Listing_Brief_2026-12-01_(1).pdf"
    ),
    Report(
        runId = 2,
        reportDate = "2025-07-21",
        query = "Junior Software Engineer",
        location = "Ireland",
        jobCount = 5,
        avgScore = 87,
        topScore = 92,
        fileName = "Job_Listing_Brief_2025-07-21_(2).pdf"
    ),
    Report(
        runId = 3,
        reportDate = "2026-03-16",
        query = "AI prompt writer",
        location = "",
        jobCount = 0,
        avgScore = null,
        topScore = null,
        fileName = "Job_Listing_Brief_2026-03-16_(3).pdf"
    )

)

val fakeSetupStatus =
    SetupStatus(
        cvSet = true,
        searchSet = false,
        firstReportGenerated = false,
        setupComplete = false
    )
