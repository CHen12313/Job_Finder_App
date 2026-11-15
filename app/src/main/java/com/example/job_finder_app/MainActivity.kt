package com.example.job_finder_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.job_finder_app.ui.navigation.AppNavigation
import com.example.job_finder_app.ui.theme.Job_Finder_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            Job_Finder_AppTheme {
                AppNavigation()
            }
        }
    }
}
