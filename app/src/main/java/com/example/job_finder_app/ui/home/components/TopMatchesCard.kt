package com.example.job_finder_app.ui.home.components

import android.R
import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.job_finder_app.data.fake.fakeMatches
import com.example.job_finder_app.data.model.JobCard
import com.example.job_finder_app.ui.theme.Job_Finder_AppTheme
import com.example.job_finder_app.ui.theme.palette

@Composable
fun JobRow(job: JobCard) {
    Row(
        // align children vertically centered, gap between them
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 1. icon tile: a Box with a background + rounded corners, emoji Text inside
        // 2. Column that takes up the remaining space:
        //      job title (bold) + "company · location" (smaller, muted)
        // 3. MatchRing(score)  ← leave as a plain Text(score) for now

        Box(
            modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.palette.surf2, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("💼")
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = job.jobTitle, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
            Text(
                text = "${job.companyName} · ${job.location}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.palette.muted
            )
        }
        MatchRing(score = job.estimatedFitScore)
    }
}

@Preview(showBackground = true)
@Composable
private fun JobRowPreview() {
    Job_Finder_AppTheme {
        JobRow(job = fakeMatches[0])
    }
}

@Composable
fun TopMatchesCard(matches: List<JobCard>) {
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
            // header Row: "Top matches" + "View all →"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Top Matches", style = MaterialTheme.typography.titleLarge)
                Text(
                    "View All →",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.palette.primaryDeep
                )
            }
            // one JobRow per match. How do you loop over a list here?
            for (job in matches) {
                JobRow(job)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF4EE)
@Composable
private fun TopMatchesCardPreview() {
    Job_Finder_AppTheme {
        TopMatchesCard(fakeMatches)
    }
}

@Composable
fun MatchRing(score: Int) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        // the ring
        CircularProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier.size(54.dp),
            color = MaterialTheme.palette.good,
            trackColor = MaterialTheme.palette.track,
        )
        // the number on top
        Text(text = "$score",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.palette.goodDeep,
            style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchRingPreview() {
    Job_Finder_AppTheme {
        MatchRing(78)
    }
}