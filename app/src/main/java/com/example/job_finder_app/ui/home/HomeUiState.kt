package com.example.job_finder_app.ui.home

import com.example.job_finder_app.data.model.JobCard
import com.example.job_finder_app.data.model.Skill

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Content(val matches: List<JobCard>, val skills: List<Skill>): HomeUiState
    data object NotLoggedIn : HomeUiState
    data class Error(val message: String) : HomeUiState
}