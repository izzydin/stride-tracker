package com.example.stridetracker.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stridetracker.data.local.AthleteDao
import com.example.stridetracker.data.local.AthleteEntity
import com.example.stridetracker.presentation.viewmodel.AthleteListViewModel
import com.example.stridetracker.presentation.viewmodel.AthleteListViewModelFactory

@Composable
fun AthleteListScreen(
    athleteDao: AthleteDao,
    onAthleteClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AthleteListViewModel = viewModel(
        factory = AthleteListViewModelFactory(athleteDao)
    )
) {
    val athletes by viewModel.athletes.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Athlete")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Athletes",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (athletes.isEmpty()) {
                Text(
                    text = "No athletes found. Add one to get started!",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn {
                    items(athletes) { athlete ->
                        AthleteItem(
                            athlete = athlete,
                            onClick = { onAthleteClick(athlete.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        if (showDialog) {
            AddAthleteDialog(
                onDismiss = { showDialog = false },
                onConfirm = { name ->
                    viewModel.addAthlete(name)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AthleteItem(
    athlete: AthleteEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Text(
            text = athlete.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun AddAthleteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Athlete") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Athlete Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
