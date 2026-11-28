package com.example.job_finder_app.ui.reports

import com.example.job_finder_app.data.model.Report


sealed interface ReportsUiState {
    data object Loading : ReportsUiState
    data class Content(
        val reports: List<Report>
    ) : ReportsUiState
    data object NotLoggedIn : ReportsUiState
    data class Error(val message: String) : ReportsUiState
}