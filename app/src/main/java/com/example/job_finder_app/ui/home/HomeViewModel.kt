package com.example.job_finder_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.job_finder_app.JobFinderApplication
import com.example.job_finder_app.data.fake.fakeSetupStatus
import com.example.job_finder_app.data.remote.NotLoggedInException
import com.example.job_finder_app.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository): ViewModel(){
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh(){
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            val  matches = repository.getTopMatches()
            val  skills = repository.getSkills()
            val reports = repository.listAllReports()

            val error = matches.exceptionOrNull() ?: skills.exceptionOrNull()?: reports.exceptionOrNull()

            _uiState.value = when (error){
                null -> HomeUiState.Content(matches.getOrThrow(), skills.getOrThrow(), reports.getOrThrow(), setupStatus = fakeSetupStatus)
                is NotLoggedInException -> HomeUiState.NotLoggedIn
                else -> HomeUiState.Error(error.message ?: "Something went wrong")
            }
        }
    }

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JobFinderApplication
                HomeViewModel(app.container.homeRepository)
            }
        }
    }
}
