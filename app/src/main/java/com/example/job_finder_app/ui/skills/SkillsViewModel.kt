package com.example.job_finder_app.ui.skills

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

class SkillsViewModel(private val repository: HomeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<SkillsUiState>(SkillsUiState.Loading)
    val uiState: StateFlow<SkillsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = SkillsUiState.Loading

        viewModelScope.launch {
            val skills = repository.getSkills(limit = 50)

            val error = skills.exceptionOrNull()
            _uiState.value = when (error) {
                null -> SkillsUiState.Content(skills.getOrThrow())
                is NotLoggedInException -> SkillsUiState.NotLoggedIn
                else -> SkillsUiState.Error(error.message ?: "Something went wrong")
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JobFinderApplication
                SkillsViewModel(app.container.homeRepository)
            }
        }
    }
}