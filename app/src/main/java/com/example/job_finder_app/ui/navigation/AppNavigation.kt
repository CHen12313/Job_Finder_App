package com.example.job_finder_app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import com.example.job_finder_app.ui.login.LoginScreen
import com.example.job_finder_app.ui.matches.MatchesScreen
import com.example.job_finder_app.ui.profile.ProfileScreen
import com.example.job_finder_app.ui.reports.ReportsScreen
import com.example.job_finder_app.ui.skills.SkillsScreen
import com.example.job_finder_app.ui.theme.palette

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.palette.surf) {
                    for (tab in BottomTab.entries) {
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { //clears screens back to home
                                        saveState = true  //remembers scroll position
                                    }
                                    launchSingleTop = true //stops a second copy if double tap
                                    restoreState = true //remember each tabs' scroll position
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.label)},
                            label = {Text(tab.label)},
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.palette.primary,
                                selectedTextColor = MaterialTheme.palette.primary,
                                indicatorColor = MaterialTheme.palette.primarySoft,
                                unselectedIconColor = MaterialTheme.palette.muted,
                                unselectedTextColor = MaterialTheme.palette.muted)
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
                composable(BottomTab.Home.route) {
                    HomeScreen(onLoginClick = { navController.navigate("login") })
                }
                composable("login") {
                    LoginScreen(onLoggedIn = {
                        navController.navigate(BottomTab.Home.route){
                            popUpTo(BottomTab.Home.route) { inclusive = true }
                        }
                    })
                }
                composable(BottomTab.Matches.route) { MatchesScreen() }
                composable(BottomTab.Reports.route) {ReportsScreen()}
                composable(BottomTab.Skills.route) {SkillsScreen()}
                composable(BottomTab.Profile.route) {ProfileScreen()}

            }
        }
}