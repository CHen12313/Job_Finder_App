package com.example.job_finder_app.data.remote
import com.example.job_finder_app.data.model.JobCard
import com.example.job_finder_app.data.model.Report
import com.example.job_finder_app.data.model.Skill
import com.example.job_finder_app.data.remote.model.JobDto
import com.example.job_finder_app.data.remote.model.ReportDto
import com.example.job_finder_app.data.remote.model.SkillsTrendResponse


//Mappers convert server DTOs (data.remote.model) into the UI models (data.model) that the composables draw.
// This is the one place that decides what the UI shows when the server sends null.

fun JobDto.toJobCard(): JobCard = JobCard(
    jobId = this.jobId,
    jobTitle = this.title ?: "Untitled role",
    companyName = this.company ?: "Unknown company",
    location = this.location ?: "Unknown location",
    estimatedFitScore = this.fitScore ?: 0
)

//{"AWS": [7, 1]} -> Skill(name = "AWS", missing = 7, matched = 1)
fun SkillsTrendResponse.toSkills(): List<Skill> = skills.map { (name, counts)  ->
    Skill(
        name = name,
        missing = counts.getOrElse(0) {0},
        matched = counts.getOrElse(1) {0}
    )

}

//Server report run -> the UI's Report. downloadUrl is dropped until the UI supports downloads.
fun ReportDto.toReport(): Report = Report(
    runId = this.runId.toInt(),
    reportDate = this.reportDate,
    query = this.query,
    location = this.location,
    jobCount = this.jobCount,
    avgScore = this.avgScore,
    topScore = this.topScore,
    fileName = this.filename
)