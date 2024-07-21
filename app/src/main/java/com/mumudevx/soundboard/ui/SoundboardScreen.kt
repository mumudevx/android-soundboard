package com.mumudevx.soundboard.ui

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import com.mumudevx.soundboard.MainActivity
import com.mumudevx.soundboard.R
import com.mumudevx.soundboard.model.Sound
import com.mumudevx.soundboard.viewmodel.SoundsViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mumudevx.soundboard.util.MediaUtils.setSoundAs

class SoundboardScreen : ComponentActivity()

var isPlaying: Boolean by mutableStateOf(false)

@Composable
fun SoundboardScreenContent(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    val viewModel = SoundsViewModel(LocalContext.current)

    val tabs = listOf("Tab One", "Tab Two", "Tab Three")

    val allSounds = SoundsViewModel.allSounds

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

                                if (searchText.isNotEmpty()) {
                                    selectedTabIndex = 99
                                }
                                else{
                                    selectedTabIndex = 0
                                }

                                filteredSounds = filterSounds(allSounds, searchText)

                                chunkedSounds = if (filteredSounds.isNotEmpty()) {
                                    filteredSounds.chunked((filteredSounds.size / tabs.size).takeIf { it > 0 }
                                        ?: 1)
                                } else {
                                    allSounds.chunked(allSounds.size / tabs.size)
                                }
                            },
                            label = { Text(text = "Search Sounds") },
                            modifier = Modifier.weight(1f)
                        )
                        ShowFavoriteBadge(viewModel, navController)
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

                    else -> {
                        TabContent(
                            sounds = filteredSounds
                        )
                    }
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
fun ShowDialogIfNeeded(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onCancel: () -> Unit,
    soundTitle: String,
    viewModel: SoundsViewModel,
    selectedSound: Sound?
) {
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = { Text(text = soundTitle) },
            text = {
                Column {
                    Text(
                        text = "Add to favorites",
                        modifier = Modifier
                            .clickable {
                                selectedSound?.let {
                                    viewModel.addFavorite(it)
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
                                    setSoundAs(
                                        context = context,
                                        soundId = selectedSound!!.resourceId,
                                        soundTitle = selectedSound.title,
                                        soundType = RingtoneManager.TYPE_RINGTONE
                                    )
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
                                    setSoundAs(
                                        context = context,
                                        soundId = selectedSound!!.resourceId,
                                        soundTitle = selectedSound.title,
                                        soundType = RingtoneManager.TYPE_ALARM
                                    )
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
                                    setSoundAs(
                                        context = context,
                                        soundId = selectedSound!!.resourceId,
                                        soundTitle = selectedSound.title,
                                        soundType = RingtoneManager.TYPE_NOTIFICATION
                                    )
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

@Composable
fun ShowFavoriteBadge(viewModel: SoundsViewModel, navController: NavController) {
    val favoriteSoundsCount = viewModel.favoriteSoundsCount.observeAsState(initial = 0).value
    val favoriteSounds = viewModel.favoriteSounds.observeAsState(initial = listOf())

    BadgedBox(
        badge = {
            Badge(
                modifier = Modifier.offset(x = (-12).dp, y = (8).dp),
                containerColor = Color.Black,
                contentColor = Color.White,
            ) {
                Text(
                    text = favoriteSoundsCount.toString(),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 13.sp),
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    ) {
        IconButton(onClick = {
            navController.navigate("favoriteSounds")
        }) {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = "Favorite Sounds",
                Modifier.size(34.dp),
                tint = Color.Red
            )
        }
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

    if (soundCounter >= 10) {
        if (context is MainActivity) {
            context.showInterstitialAd()
        }
        soundCounter = 0
    }
}