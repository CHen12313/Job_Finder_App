package com.example.job_finder_app.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.job_finder_app.data.fake.fakeSkills
import com.example.job_finder_app.data.model.Skill
import com.example.job_finder_app.ui.theme.Job_Finder_AppTheme
import com.example.job_finder_app.ui.theme.palette

@Composable
fun SkillRow(skill: Skill, max: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = skill.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.palette.ink,
                fontWeight = FontWeight.SemiBold
            )

            Text(text = "${skill.missing} jobs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.palette.muted
            )
        }

        LinearProgressIndicator(
            progress = { skill.missing / max.toFloat()},
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = MaterialTheme.palette.primary,
            trackColor = MaterialTheme.palette.track,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {}
        )
    }
}

@Composable
fun SkillsCard(skills: List<Skill>) {
    val top = skills.filter {it.missing > 0}
        .sortedByDescending { it.missing }
        .take(3)

    val max = top.maxOf { it.missing }
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
                Column() {
                    Text(
                        text = "Skills to work on",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = "Missing from your CV",
                        style = MaterialTheme.typography.titleSmall,
                        )
                }
            }

            for (skill in top) {
                SkillRow(skill, max)
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
private fun SkillCardPreview() {
    Job_Finder_AppTheme {
        Box(Modifier.padding(16.dp)) {
            SkillRow(skill = fakeSkills[2], max = 10)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF4EE)
@Composable
private fun SkillsCardPreview() {
    Job_Finder_AppTheme {
        Box(Modifier.padding(16.dp)) {
            SkillsCard(fakeSkills)
        }
    }
}