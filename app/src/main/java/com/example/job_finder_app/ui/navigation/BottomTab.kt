package com.example.job_finder_app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomTab(val route: String, val label: String, val icon: ImageVector) {
    Home("home", "Home", Icons.Default.Home),
    Matches("matches", "Matches", Icons.Default.Work),
    Reports("reports", "Reports", Icons.Default.Description),
    Skills("skills", "Skills", Icons.Default.Sell),
    Profile("profile", "Profile", Icons.Default.Person)
}