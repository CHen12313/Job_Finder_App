package com.example.job_finder_app.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.job_finder_app.JobFinderApplication
import com.example.job_finder_app.data.remote.NotLoggedInException
import com.example.job_finder_app.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportsViewModel(private val repository: HomeRepository) : ViewModel(){
    private val _uiState = MutableStateFlow<ReportsUiState>(ReportsUiState.Loading)
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = ReportsUiState.Loading

        viewModelScope.launch {
            val reports = repository.listAllReports()

            val error = reports.exceptionOrNull()
            _uiState.value = when (error) {
                null -> ReportsUiState.Content(reports.getOrThrow())
                is NotLoggedInException -> ReportsUiState.NotLoggedIn
                else -> ReportsUiState.Error(error.message ?: "Something went wrong")
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JobFinderApplication
                ReportsViewModel(app.container.homeRepository)
            }
        }
    }
}
