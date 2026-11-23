package com.example.job_finder_app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.job_finder_app.data.fake.fakeSetupStatus
import com.example.job_finder_app.data.model.SetupStatus
import com.example.job_finder_app.ui.theme.Job_Finder_AppTheme
import com.example.job_finder_app.ui.theme.palette

@Composable
fun SetupStepRow(
    number: Int,
    title: String,
    description: String,
    done: Boolean,
    current: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (current) MaterialTheme.palette.primary else MaterialTheme.palette.line
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .clickable(enabled = !done) { onClick() }
            .border(1.5.dp, borderColor, RoundedCornerShape(13.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val circleColor = if (done) MaterialTheme.palette.goodSoft else MaterialTheme.palette.surf2
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(circleColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (done) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Done",
                    tint = MaterialTheme.palette.good,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = number.toString(),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.palette.muted
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = if (done) MaterialTheme.palette.muted else MaterialTheme.palette.ink
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.palette.muted
            )
        }
        if (current) {
            Text(
                text = "You're here",
                color = MaterialTheme.palette.primary,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun SetupCard(
    status: SetupStatus,
    onUploadCv: () -> Unit,
    onSetSearch: () -> Unit,
    onGenerateReport: () -> Unit,
) {
    if (status.setupComplete) return
    val cvCurrent = !status.cvSet
    val searchCurrent = status.cvSet && !status.searchSet
    val reportCurrent = status.cvSet && status.searchSet && !status.firstReportGenerated
    val doneCount = listOf(status.cvSet, status.searchSet, status.firstReportGenerated).count { it }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Finish setting up",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$doneCount of 3 done",
                    color = MaterialTheme.palette.muted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            SetupStepRow(
                number = 1,
                title = "Upload your CV",
                description = "So we can match you to jobs",
                done = status.cvSet,
                current = cvCurrent,
                onClick = onUploadCv
            )
            SetupStepRow(
                number = 2,
                title = "Set up your search",
                description = "Job title, location and filters",
                done = status.searchSet,
                current = searchCurrent,
                onClick = onSetSearch
            )
            SetupStepRow(
                number = 3,
                title = "Generate first report",
                description = "See your ranked matches",
                done = status.firstReportGenerated,
                current = reportCurrent,
                onClick = onGenerateReport
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SetupStepPreview() {
    Job_Finder_AppTheme {
        Box(Modifier.padding(16.dp)) {
            SetupStepRow(
                number = 2,
                title = "Set up your search",
                description = "Job title, location and filters",
                done = false,
                current = true,
                onClick = {})
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF4EE)
@Composable
private fun SetupCardPreview() {
    Job_Finder_AppTheme {
        Box(Modifier.padding(16.dp)) {
            SetupCard(
                status = fakeSetupStatus,
                onUploadCv = {},
                onSetSearch = {},
                onGenerateReport = {})
        }
    }
}