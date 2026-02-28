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
import com.example.stridetracker.data.repository.AthleteRepositoryImpl
import com.example.stridetracker.data.repository.SessionRepositoryImpl
import com.example.stridetracker.domain.usecase.DeleteAthleteUseCase
import com.example.stridetracker.domain.usecase.DeleteSessionUseCase
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
        
        // Manual DI for simplicity in this project
        val sessionRepository = SessionRepositoryImpl(sessionDao)
        val deleteSessionUseCase = DeleteSessionUseCase(sessionRepository)
        
        val athleteRepository = AthleteRepositoryImpl(athleteDao)
        val deleteAthleteUseCase = DeleteAthleteUseCase(athleteRepository)
        
        enableEdgeToEdge()
        setContent {
            StrideTrackerTheme {
                StrideTrackerNavHost(sessionDao, athleteDao, deleteSessionUseCase, deleteAthleteUseCase)
            }
        }
    }
}

@Composable
fun StrideTrackerNavHost(
    sessionDao: SessionDao, 
    athleteDao: AthleteDao,
    deleteSessionUseCase: DeleteSessionUseCase,
    deleteAthleteUseCase: DeleteAthleteUseCase
) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "athletes") {
        composable("athletes") {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                AthleteListScreen(
                    athleteDao = athleteDao,
                    deleteAthleteUseCase = deleteAthleteUseCase,
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
            MeasurementScreen(
                athleteId = athleteId,
                sessionDao = sessionDao,
                navController = navController,
                onNavigateToHistory = { navController.navigate("history/$athleteId") }
            )
        }
        composable(
            route = "history/{athleteId}",
            arguments = listOf(navArgument("athleteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val athleteId = backStackEntry.arguments?.getLong("athleteId") ?: 0L
            SessionHistoryScreen(
                athleteId = athleteId,
                sessionDao = sessionDao,
                navController = navController,
                onSessionClick = { sessionId -> 
                    navController.navigate("detail/$sessionId")
                },
                onStartNewSession = {
                    navController.navigate("measurement/$athleteId")
                }
            )
        }
        composable(
            route = "detail/{sessionId}",
            arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
            SessionDetailScreen(
                sessionId = sessionId,
                sessionDao = sessionDao,
                deleteSessionUseCase = deleteSessionUseCase,
                navController = navController
            )
        }
    }
}
