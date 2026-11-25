package com.example.job_finder_app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.job_finder_app.JobFinderApplication
import com.example.job_finder_app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update{ it.copy(username = value, error = null)}
    }

    fun onPasswordChange(value: String) {
        _uiState.update{ it.copy(password = value, error = null) }
    }
    
    fun submit() {
        val state = _uiState.value
        if (state.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            repository.login(state.username, state.password)
                .onSuccess {
                    _uiState.update{ it.copy(isLoading = false, loggedIn = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message ?: "Login failed", isLoading = false) }
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JobFinderApplication
                LoginViewModel(app.container.authRepository)
            }
        }
    }
}
