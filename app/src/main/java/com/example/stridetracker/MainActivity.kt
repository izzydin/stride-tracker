package com.example.stridetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.stridetracker.data.local.AppDatabase
import com.example.stridetracker.presentation.screen.MeasurementScreen
import com.example.stridetracker.ui.theme.StrideTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val sessionDao = database.sessionDao()
        
        enableEdgeToEdge()
        setContent {
            StrideTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MeasurementScreen(sessionDao = sessionDao)
                }
            }
        }
    }
}
