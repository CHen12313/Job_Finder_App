package com.example.job_finder_app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.job_finder_app.ui.home.HomeScreen
import com.example.job_finder_app.ui.matches.MatchesScreen
import com.example.job_finder_app.ui.profile.ProfileScreen
import com.example.job_finder_app.ui.reports.ReportsScreen
import com.example.job_finder_app.ui.skills.SkillsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // currentBackStackEntryAsState() returns whatever screen is showing and updates UI when it changes
    // ? is a safe call, returns null instead of crashing
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        Scaffold(
            bottomBar = {
                NavigationBar {
                    for (tab in BottomTab.entries) {
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { //clears screens back to home
                                        saveState = true    // Remember each tab's scroll position when you switch away
                                    }
                                    launchSingleTop = true  // stops a 2nd copy of the same screen if tapped twice
                                    restoreState = true // Remember each tab's scroll position when you switch away
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label)},
                            label = {Text(tab.label)}
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = BottomTab.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomTab.Home.route) { HomeScreen() }
                composable(BottomTab.Matches.route) { MatchesScreen() }
                composable(BottomTab.Reports.route) {ReportsScreen()}
                composable(BottomTab.Skills.route) {SkillsScreen()}
                composable(BottomTab.Profile.route) {ProfileScreen()}

            }
        }
}