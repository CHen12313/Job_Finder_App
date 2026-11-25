package com.example.job_finder_app.ui.matches

import com.example.job_finder_app.data.model.JobCard

sealed interface MatchesUiState {
    data object Loading : MatchesUiState
    data class Content(
        val jobs: List<JobCard>
    ) : MatchesUiState
    data object NotLoggedIn : MatchesUiState
    data class Error(val message: String) : MatchesUiState
}