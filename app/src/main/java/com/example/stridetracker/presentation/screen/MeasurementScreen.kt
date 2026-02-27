package com.example.stridetracker.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stridetracker.presentation.viewmodel.MeasurementViewModel
import java.util.Locale

@Composable
fun MeasurementScreen(
    viewModel: MeasurementViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = formatElapsedTime(uiState.elapsedTimeMillis),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Total Strides: ${uiState.totalStrides}",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Current Segment: ${uiState.currentSegmentStrides}",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = if (uiState.isRunning) "Running" else "Stopped",
            color = if (uiState.isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.onDistanceClick() }) {
                Text("Distance")
            }
            Button(onClick = { viewModel.onStartStop() }) {
                Text(if (uiState.isRunning) "Stop" else "Start")
            }
            Button(onClick = { viewModel.onStrideClick() }) {
                Text("Stride")
            }
        }
    }
}

private fun formatElapsedTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    val tenths = (millis / 100) % 10
    
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d.%d", hours, minutes, seconds, tenths)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d.%d", minutes, seconds, tenths)
    }
}
