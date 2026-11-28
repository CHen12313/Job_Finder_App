package com.example.job_finder_app.ui.matches

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

class MatchesViewModel(private val repository: HomeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = MatchesUiState.Loading

        viewModelScope.launch {
            val matches = repository.getTopMatches(limit = 50)

            val error = matches.exceptionOrNull()
            _uiState.value = when (error) {
                null -> MatchesUiState.Content(matches.getOrThrow())
                is NotLoggedInException -> MatchesUiState.NotLoggedIn
                else -> MatchesUiState.Error(error.message ?: "Something went wrong")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JobFinderApplication
                MatchesViewModel(app.container.homeRepository)
            }
        }
    }
}
