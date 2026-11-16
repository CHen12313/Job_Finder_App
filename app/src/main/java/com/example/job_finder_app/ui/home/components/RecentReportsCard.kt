package com.example.job_finder_app.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.job_finder_app.data.fake.fakeReports
import com.example.job_finder_app.data.model.Report
import com.example.job_finder_app.ui.theme.Job_Finder_AppTheme
import com.example.job_finder_app.ui.theme.palette
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReportRow(
    report: Report,
    onReportClick: (Int) -> Unit,
    onDownloadClick: (Int) -> Unit,
) {
    val formattedDate = LocalDate.parse(report.reportDate)
        .format(DateTimeFormatter.ofPattern("d MMM yyyy"))
    val jobWord = if (report.jobCount == 1) "job" else "jobs"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onReportClick(report.runId) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.palette.surf2, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.palette.primary,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = report.query ?: "Job search report",
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$formattedDate · ${report.jobCount} $jobWord",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.palette.muted
            )
        }
        if (report.avgScore != null) {
            Text(
                text = "avg ${report.avgScore}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.palette.muted
            )
        }
        IconButton(onClick = { onDownloadClick(report.runId) }) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download PDF",
                tint = MaterialTheme.palette.muted
            )
        }
    }
}

@Composable
fun RecentReportsCard(
    reports: List<Report>,
    onAllReportsClick: () -> Unit,
    onReportClick: (Int) -> Unit,
    onDownloadClick: (Int) -> Unit,
) {
    val recent = reports.take(3)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    )
    {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Recent Reports",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "All Reports →",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.palette.primaryDeep,
                    modifier = Modifier
                        .clickable { onAllReportsClick() }
                        .padding(8.dp)
                )
            }
            if (recent.isEmpty()) {
                Text(
                    text = "No reports yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.palette.muted
                )
            } else {
                for (report in recent) {
                    ReportRow(report, onReportClick, onDownloadClick)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportRowPreview() {
    Job_Finder_AppTheme {
        Box(Modifier.padding(16.dp)) {
            ReportRow(report = fakeReports[2], onReportClick = {}, onDownloadClick = {})
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF4EE)
@Composable
private fun RecentReportsCardPreview() {
    Job_Finder_AppTheme {
        Box(Modifier.padding(16.dp)) {
            RecentReportsCard(fakeReports, onAllReportsClick = {}, onReportClick = {}, onDownloadClick = {})
        }
    }
}