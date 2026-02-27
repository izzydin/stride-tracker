package com.example.stridetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stridetracker.data.local.AppDatabase
import com.example.stridetracker.data.local.SessionDao
import com.example.stridetracker.presentation.screen.MeasurementScreen
import com.example.stridetracker.presentation.screen.SessionDetailScreen
import com.example.stridetracker.presentation.screen.SessionHistoryScreen
import com.example.stridetracker.ui.theme.StrideTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val sessionDao = database.sessionDao()
        
        enableEdgeToEdge()
        setContent {
            StrideTrackerTheme {
                StrideTrackerNavHost(sessionDao)
            }
        }
    }
}

@Composable
fun StrideTrackerNavHost(sessionDao: SessionDao) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "measurement") {
        composable("measurement") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MeasurementScreen(
                    sessionDao = sessionDao,
                    onNavigateToHistory = { navController.navigate("history") },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        composable("history") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                SessionHistoryScreen(
                    sessionDao = sessionDao,
                    onBack = { navController.popBackStack() },
                    onSessionClick = { sessionId -> 
                        navController.navigate("detail/$sessionId")
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        composable(
            route = "detail/{sessionId}",
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                SessionDetailScreen(
                    sessionId = sessionId,
                    sessionDao = sessionDao,
                    onBack = { navController.popBackStack() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
