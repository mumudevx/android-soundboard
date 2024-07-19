package com.mumudevx.soundboard.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mumudevx.soundboard.model.Sound
import com.mumudevx.soundboard.viewmodel.SoundsViewModel

class FavoriteSoundsScreen : ComponentActivity()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteSoundsScreenContent(navController: NavController) {
    val viewModel = SoundsViewModel(LocalContext.current)

    val favoriteSounds by viewModel.favoriteSounds.observeAsState(initial = listOf())

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Favorite Sounds") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("soundboard") }) {
                            Icon(Icons.Filled.Home, contentDescription = "Go to Soundboard.kt")
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                FavoriteContent(favoriteSounds = favoriteSounds.ifEmpty { emptyList() })
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FavoriteContent(favoriteSounds: List<Sound>) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var selectedSound by remember { mutableStateOf<Sound?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp)
            .padding(bottom = 75.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        favoriteSounds.chunked(2).forEach { soundsInRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                soundsInRow.forEachIndexed { index, sound ->
                    Button(
                        onClick = {
                            playSound(
                                context = context,
                                soundId = sound.resourceId
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = sound.title,
                            modifier = Modifier.combinedClickable(
                                onClick = {
                                    playSound(
                                        context = context,
                                        soundId = sound.resourceId
                                    )
                                },
                                onLongClick = {
                                    selectedSound = sound
                                    showDialog = true
                                }
                            )
                        )
                    }
                    if (index < favoriteSounds.size - 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (soundsInRow.size == 1) {
                        Box(modifier = Modifier.weight(1f)) {}
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        ShowFavoriteDialogIfNeeded(
            showDialog = showDialog,
            onDismissRequest = { showDialog = false },
            onCancel = {
                showDialog = false
            },
            selectedSound?.title ?: "",
            viewModel = SoundsViewModel(context),
            selectedSound
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowFavoriteDialogIfNeeded(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    soundTitle: String,
    viewModel: SoundsViewModel,
    selectedSound: Sound?
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = { Text(text = soundTitle) },
            text = {
                Column {
                    Text(
                        text = "Remove from favorites",
                        modifier = Modifier
                            .clickable {
                                selectedSound?.let {
                                    viewModel.removeFavorite(it)
                                    onDismissRequest()
                                }
                            }
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Set as ringtone",
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    // Handle click action
                                    println("Set as ringtone clicked!")
                                    onDismissRequest()
                                }
                            )
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Set as alarm",
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    // Handle click action
                                    println("Set as alarm clicked!")
                                    onDismissRequest()
                                }
                            )
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Set as notification",
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    // Handle click action
                                    println("Set as notification clicked!")
                                    onDismissRequest()
                                }
                            )
                            .padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { onCancel() }) {
                    Text("Close")
                }
            }
        )
    }
}