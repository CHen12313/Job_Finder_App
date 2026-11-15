package com.example.job_finder_app.data.repository

import com.example.job_finder_app.data.model.JobCard
import com.example.job_finder_app.data.model.Skill
import com.example.job_finder_app.data.remote.JobFinderApi
import com.example.job_finder_app.data.remote.bodyOrThrow
import com.example.job_finder_app.data.remote.toJobCard
import com.example.job_finder_app.data.remote.toSkills


class HomeRepository(private val api: JobFinderApi){
    suspend fun getTopMatches(limit: Int = 3): Result<List<JobCard>> = runCatching {
        api.getReportJobs(limit = limit)
            .bodyOrThrow()
            .jobs
            .map {it.toJobCard()}
    }

    suspend fun getSkills(limit: Int = 3): Result<List<Skill>> = runCatching {
        api.getSkillsTrend(limit = limit)
            .bodyOrThrow()
            .toSkills()
    }
}