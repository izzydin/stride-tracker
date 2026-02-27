package com.example.stridetracker.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stridetracker.domain.model.SessionState
import com.example.stridetracker.presentation.viewmodel.MeasurementViewModel
import java.util.Locale

@Composable
fun MeasurementScreen(
    viewModel: MeasurementViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        MeasurementLandscape(uiState, viewModel)
    } else {
        MeasurementPortrait(uiState, viewModel)
    }
}

@Composable
private fun MeasurementPortrait(
    uiState: SessionState,
    viewModel: MeasurementViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StatsSection(uiState)

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ControlButtons(uiState, viewModel, Modifier.weight(1f))
        }
    }
}

@Composable
private fun MeasurementLandscape(
    uiState: SessionState,
    viewModel: MeasurementViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StatsSection(uiState)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            ControlButtons(uiState, viewModel, Modifier.fillMaxWidth().height(80.dp))
        }
    }
}

@Composable
private fun StatsSection(uiState: SessionState) {
    Text(
        text = formatElapsedTime(uiState.elapsedTimeMillis),
        style = MaterialTheme.typography.displayLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = "Total Strides: ${uiState.totalStrides}",
        style = MaterialTheme.typography.headlineMedium
    )
    Text(
        text = "Current Segment: ${uiState.currentSegmentStrides}",
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.secondary
    )
    Text(
        text = if (uiState.isRunning) "RUNNING" else "STOPPED",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = if (uiState.isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun ControlButtons(
    uiState: SessionState,
    viewModel: MeasurementViewModel,
    buttonModifier: Modifier = Modifier
) {
    Button(
        onClick = { viewModel.onDistanceClick() },
        modifier = buttonModifier.padding(horizontal = 4.dp),
        enabled = uiState.isRunning
    ) {
        Text("DISTANCE", style = MaterialTheme.typography.titleMedium)
    }

    Button(
        onClick = { viewModel.onStartStop() },
        modifier = buttonModifier.padding(horizontal = 4.dp),
        colors = if (uiState.isRunning) {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
        } else {
            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    ) {
        Text(if (uiState.isRunning) "STOP" else "START", style = MaterialTheme.typography.titleMedium)
    }

    Button(
        onClick = { viewModel.onStrideClick() },
        modifier = buttonModifier.padding(horizontal = 4.dp),
        enabled = uiState.isRunning
    ) {
        Text("STRIDE", style = MaterialTheme.typography.titleMedium)
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
