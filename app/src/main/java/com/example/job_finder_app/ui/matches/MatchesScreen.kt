package com.example.job_finder_app.ui.matches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.job_finder_app.ui.home.components.CenteredMessage
import com.example.job_finder_app.ui.home.components.JobRow

@Composable
fun MatchesScreen(onLoginClick: () -> Unit, viewModel: MatchesViewModel = viewModel(factory = MatchesViewModel.Factory)) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MatchesContent(state = state, onRetry = viewModel::refresh, onLoginClick = onLoginClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesContent(state: MatchesUiState, onRetry: () -> Unit, onLoginClick: () -> Unit,) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Matches", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }) { innerPadding ->
        when (state) {
            MatchesUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            is MatchesUiState.Content ->
                if (state.jobs.isEmpty()) {
                    CenteredMessage(
                        innerPadding,
                        "No matches yet. Generate a report to start matching jobs."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(innerPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(state.jobs) { job ->
                            // JobRow(job) wrapped in a Card
                            Card(
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Box(Modifier.padding(16.dp)) { JobRow(job) }
                            }
                        }
                    }
                }

            MatchesUiState.NotLoggedIn -> CenteredMessage(
                innerPadding,
                "Log in to see your job matches.",
                onRetry = onLoginClick,
                buttonText = "Log in"
            )

            is MatchesUiState.Error -> CenteredMessage(innerPadding, state.message, onRetry)
        }
    }
}