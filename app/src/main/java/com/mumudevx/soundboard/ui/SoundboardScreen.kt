package com.mumudevx.soundboard.ui

import android.content.Context
import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mumudevx.soundboard.MainActivity
import com.mumudevx.soundboard.R
import com.mumudevx.soundboard.model.Sound
import kotlinx.coroutines.launch

class SoundboardScreen : ComponentActivity()

var isPlaying: Boolean by mutableStateOf(false)

@Composable
fun SoundboardScreenContent(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    val tabs = listOf("Tab One", "Tab Two", "Tab Three")

    val allSounds = listOf(
        Sound("Sound One", R.raw.sound_1),
        Sound("Sound Two", R.raw.sound_2),
        Sound("Sound Three", R.raw.sound_3),
        Sound("Sound Four", R.raw.sound_4),
        Sound("Sound Five", R.raw.sound_5),
        Sound("Sound Six", R.raw.sound_6),
        Sound("Sound Seven", R.raw.sound_7),
        Sound("Sound Eight", R.raw.sound_8),
        Sound("Sound Nine", R.raw.sound_9),
        Sound("Sound Ten", R.raw.sound_10),
        Sound("Sound Eleven", R.raw.sound_11),
        Sound("Sound Twelve", R.raw.sound_12),
        Sound("Sound Thirteen", R.raw.sound_13),
        Sound("Sound Fourteen", R.raw.sound_14),
        Sound("Sound Fifteen", R.raw.sound_15)
    )

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchText by remember { mutableStateOf("") }
    var filteredSounds by remember { mutableStateOf(allSounds) }
    var chunkedSounds by remember { mutableStateOf(allSounds.chunked(allSounds.size / tabs.size)) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { it ->
                                searchText = it
                                filteredSounds = filterSounds(allSounds, searchText)

                                chunkedSounds = if (filteredSounds.isNotEmpty()) {
                                    filteredSounds.chunked((filteredSounds.size / tabs.size).takeIf { it > 0 }
                                        ?: 1)
                                } else {
                                    allSounds.chunked(allSounds.size / tabs.size)
                                }
                            },
                            label = { Text("Search Sounds") },
                            modifier = Modifier
                                .weight(1f)
                        )
                        IconButton(onClick = {
                            println("Favorites Clicked!")
                            navController.navigate("favoriteSounds")
                        }) {
                            Icon(
                                Icons.Filled.Favorite,
                                contentDescription = "Favorite Sounds",
                                tint = Color.Red
                            )
                        }
                    }

                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                text = { Text(title) },
                                selected = selectedTabIndex == index,
                                onClick = {
                                    coroutineScope.launch {
                                        selectedTabIndex = index
                                    }
                                }
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                if (isPlaying) {
                    FloatingActionButton(
                        onClick = {
                            mediaPlayer.stop()
                            isPlaying = false
                        },
                        containerColor = Color.Red,
                        modifier = Modifier
                            .padding(bottom = 90.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.crop_square),
                            contentDescription = "Stop",
                            tint = Color.White
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                when (selectedTabIndex) {
                    0 -> TabContent(
                        sounds = if (chunkedSounds.isNotEmpty()) chunkedSounds[0] else emptyList()
                    )

                    1 -> TabContent(
                        sounds = if (chunkedSounds.size > 1) chunkedSounds[1] else emptyList()
                    )

                    2 -> TabContent(
                        sounds = if (chunkedSounds.size > 2) chunkedSounds[2] else emptyList()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabContent(sounds: List<Sound>) {
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
        sounds.chunked(2).forEach { soundsInRow ->
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
                    if (index < sounds.size - 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (soundsInRow.size == 1) {
                        Box(modifier = Modifier.weight(1f)) {}
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        ShowDialogIfNeeded(
            showDialog = showDialog,
            onDismissRequest = { showDialog = false },
            onCancel = {
                // Handle cancel action
                showDialog = false
            },
            selectedSound?.title ?: ""
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowDialogIfNeeded(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    soundTitle: String
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = { Text(text = soundTitle) },
            text = {
                Column {
                    Text(
                        text = "Add to favorites",
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    // Handle click action
                                    println("Add to favorites clicked!")
                                }
                            )
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

fun filterSounds(sounds: List<Sound>, searchText: String): List<Sound> {
    return sounds.filter { it.title.contains(searchText, ignoreCase = true) }
}

var mediaPlayer = MediaPlayer()
var soundCounter = 0
fun playSound(context: Context, soundId: Int) {
    mediaPlayer.stop()
    mediaPlayer.release()

    mediaPlayer = MediaPlayer.create(context, soundId)
    mediaPlayer.start()
    isPlaying = true

    soundCounter++
    println("Sound Counter: $soundCounter")

    if (soundCounter >= 10) {
        if (context is MainActivity) {
            context.showInterstitialAd()
        }
        soundCounter = 0
    }
}