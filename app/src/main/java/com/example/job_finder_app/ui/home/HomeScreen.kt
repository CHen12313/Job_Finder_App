package com.example.job_finder_app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.job_finder_app.data.fake.fakeMatches
import com.example.job_finder_app.data.fake.fakeSkills
import com.example.job_finder_app.data.fake.fakeReports
import com.example.job_finder_app.ui.home.components.SkillsCard
import com.example.job_finder_app.ui.home.components.TopMatchesCard
import com.example.job_finder_app.ui.theme.Job_Finder_AppTheme
import com.example.job_finder_app.ui.home.components.RecentReportsCard

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(state = state, onRetry = viewModel::refresh)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(state: HomeUiState, onRetry: () -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Prospex", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        when (state) {
            HomeUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is HomeUiState.Content -> LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { TopMatchesCard(state.matches, onViewAllClick = {}) }
                item { SkillsCard(state.skills, onSeeAllClick = {}) }
                item {
                    RecentReportsCard(
                        reports = state.reports,
                        onAllReportsClick = {},
                        onReportClick = {},
                        onDownloadClick = {}
                    )
                }
            }

            HomeUiState.NotLoggedIn -> CenteredMessage(
                innerPadding,
                "Log in to see your job matches."
            )

            is HomeUiState.Error -> CenteredMessage(innerPadding, state.message, onRetry)
        }
    }
}

@Composable
private fun CenteredMessage(padding: PaddingValues, text: String, onRetry: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text, textAlign = TextAlign.Center)
        if (onRetry != null) {
            Button(onClick = onRetry) { Text("Try Again") }
        }
    }
}

//preview uses fake data so that no server is needed
@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    Job_Finder_AppTheme() {
        HomeContent(state = HomeUiState.Content(fakeMatches, fakeSkills, fakeReports), onRetry = {})
    }
}
