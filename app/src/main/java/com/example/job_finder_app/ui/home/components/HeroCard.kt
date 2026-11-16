package com.example.job_finder_app.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.job_finder_app.ui.theme.Job_Finder_AppTheme
import com.example.job_finder_app.ui.theme.palette


@Composable
fun HeroCard(onRunSearch: () -> Unit, onAdjustSearch: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "WELCOME BACK",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.palette.muted,
                letterSpacing = 2.sp
            )
            Text(
                text = "Good to see you",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your search is set and your CV is on file",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.palette.muted
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onRunSearch,
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.palette.primary,
                        contentColor = MaterialTheme.palette.primaryInk
                    )
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Run search", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onAdjustSearch,
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.palette.surf2,
                        contentColor = MaterialTheme.palette.ink
                    )
                ) {
                        Icon(
                            Icons.Default.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Adjust search", fontWeight = FontWeight.SemiBold)
                    }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF4EE, widthDp = 360)
@Composable
private fun HeroCardPreview() {
    Job_Finder_AppTheme {
        Box(Modifier.padding(16.dp)) {
            HeroCard(onRunSearch = {}, onAdjustSearch = {})
        }
    }
}