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
import com.example.stridetracker.data.local.AthleteDao
import com.example.stridetracker.presentation.screen.MeasurementScreen
import com.example.stridetracker.presentation.screen.SessionDetailScreen
import com.example.stridetracker.presentation.screen.SessionHistoryScreen
import com.example.stridetracker.presentation.screen.AthleteListScreen
import com.example.stridetracker.ui.theme.StrideTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val sessionDao = database.sessionDao()
        val athleteDao = database.athleteDao()
        
        enableEdgeToEdge()
        setContent {
            StrideTrackerTheme {
                StrideTrackerNavHost(sessionDao, athleteDao)
            }
        }
    }
}

@Composable
fun StrideTrackerNavHost(sessionDao: SessionDao, athleteDao: AthleteDao) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "athletes") {
        composable("athletes") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AthleteListScreen(
                    athleteDao = athleteDao,
                    onAthleteClick = { athleteId -> 
                        navController.navigate("history/$athleteId")
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        composable(
            route = "measurement/{athleteId}",
            arguments = listOf(navArgument("athleteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val athleteId = backStackEntry.arguments?.getLong("athleteId") ?: 0L
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MeasurementScreen(
                    athleteId = athleteId,
                    sessionDao = sessionDao,
                    onNavigateToHistory = { navController.navigate("history/$athleteId") },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        composable(
            route = "history/{athleteId}",
            arguments = listOf(navArgument("athleteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val athleteId = backStackEntry.arguments?.getLong("athleteId") ?: 0L
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                SessionHistoryScreen(
                    athleteId = athleteId,
                    sessionDao = sessionDao,
                    onBack = { navController.popBackStack() },
                    onSessionClick = { sessionId -> 
                        navController.navigate("detail/$sessionId")
                    },
                    onStartNewSession = {
                        navController.navigate("measurement/$athleteId")
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
