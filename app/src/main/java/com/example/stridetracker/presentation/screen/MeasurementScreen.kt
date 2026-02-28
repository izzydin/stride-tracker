package com.example.stridetracker.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.stridetracker.data.local.SessionDao
import com.example.stridetracker.domain.model.SessionState
import com.example.stridetracker.presentation.viewmodel.MeasurementViewModel
import com.example.stridetracker.presentation.viewmodel.MeasurementViewModelFactory
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementScreen(
    athleteId: Long,
    sessionDao: SessionDao,
    navController: NavController,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MeasurementViewModel = viewModel(
        factory = MeasurementViewModelFactory(athleteId, sessionDao)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Measurement") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "History"
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (isLandscape) {
                LandscapeMeasurementLayout(uiState, viewModel)
            } else {
                PortraitMeasurementLayout(uiState, viewModel)
            }
        }
    }
}

@Composable
private fun ChronometerDisplay(elapsedTimeMillis: Long) {
    Text(
        text = formatElapsedTime(elapsedTimeMillis),
        style = MaterialTheme.typography.displayMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PortraitMeasurementLayout(
    uiState: SessionState,
    viewModel: MeasurementViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        StatsSection(uiState)

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButtons(uiState, viewModel)
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun LandscapeMeasurementLayout(
    uiState: SessionState,
    viewModel: MeasurementViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Left Side: Distance
        MeasurementButton(
            onClick = { viewModel.onDistanceClick() },
            icon = Icons.Default.Straighten,
            label = "DIST",
            enabled = uiState.isRunning
        )

        Spacer(modifier = Modifier.weight(1f))

        // Center: Chronometer & Start/Stop
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(2f)
        ) {
            ChronometerDisplay(uiState.elapsedTimeMillis)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Strides: ${uiState.totalStrides}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = if (uiState.isRunning) "RUNNING" else "STOPPED",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (uiState.isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            MeasurementButton(
                onClick = { viewModel.onStartStop() },
                icon = if (uiState.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                label = if (uiState.isRunning) "STOP" else "START",
                colors = if (uiState.isRunning) {
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                } else {
                    ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Right Side: Stride
        MeasurementButton(
            onClick = { viewModel.onStrideClick() },
            icon = Icons.Default.DirectionsRun,
            label = "STRIDE",
            enabled = uiState.isRunning
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatsSection(uiState: SessionState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ChronometerDisplay(uiState.elapsedTimeMillis)
        
        Spacer(modifier = Modifier.height(16.dp))

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
}

@Composable
private fun ControlButtons(
    uiState: SessionState,
    viewModel: MeasurementViewModel
) {
    MeasurementButton(
        onClick = { viewModel.onDistanceClick() },
        icon = Icons.Default.Straighten,
        label = "DIST",
        enabled = uiState.isRunning
    )

    MeasurementButton(
        onClick = { viewModel.onStartStop() },
        icon = if (uiState.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
        label = if (uiState.isRunning) "STOP" else "START",
        colors = if (uiState.isRunning) {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        } else {
            ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    )

    MeasurementButton(
        onClick = { viewModel.onStrideClick() },
        icon = Icons.Default.DirectionsRun,
        label = "STRIDE",
        enabled = uiState.isRunning
    )
}

@Composable
private fun MeasurementButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors()
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = colors
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
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
