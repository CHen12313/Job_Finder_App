package com.example.job_finder_app.ui.skills

import com.example.job_finder_app.data.model.Skill

interface SkillsUiState {
    data object Loading : SkillsUiState
    data class Content(
        val skills: List<Skill>
    ) : SkillsUiState
    data object NotLoggedIn : SkillsUiState
    data class Error(val message: String) : SkillsUiState
}