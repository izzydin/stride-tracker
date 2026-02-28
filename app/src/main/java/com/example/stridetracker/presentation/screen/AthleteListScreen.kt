package com.example.stridetracker.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stridetracker.data.local.AthleteDao
import com.example.stridetracker.data.local.AthleteEntity
import com.example.stridetracker.domain.usecase.DeleteAthleteUseCase
import com.example.stridetracker.presentation.viewmodel.AthleteListViewModel
import com.example.stridetracker.presentation.viewmodel.AthleteListViewModelFactory

@Composable
fun AthleteListScreen(
    athleteDao: AthleteDao,
    deleteAthleteUseCase: DeleteAthleteUseCase,
    onAthleteClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AthleteListViewModel = viewModel(
        factory = AthleteListViewModelFactory(athleteDao, deleteAthleteUseCase)
    )
) {
    val athletes by viewModel.athletes.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var athleteToDelete by remember { mutableStateOf<AthleteEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
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
                            onClick = { onAthleteClick(athlete.id) },
                            onDeleteClick = { athleteToDelete = athlete }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        if (showAddDialog) {
            AddAthleteDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name ->
                    viewModel.addAthlete(name)
                    showAddDialog = false
                }
            )
        }

        athleteToDelete?.let { athlete ->
            AlertDialog(
                onDismissRequest = { athleteToDelete = null },
                title = { Text("Delete Athlete") },
                text = { Text("Are you sure you want to delete ${athlete.name}? All their sessions will also be deleted.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteAthlete(athlete.id.toString())
                            athleteToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { athleteToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun AthleteItem(
    athlete: AthleteEntity,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = athlete.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Athlete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
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
