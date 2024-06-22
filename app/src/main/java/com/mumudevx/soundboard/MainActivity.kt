package com.mumudevx.soundboard

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundboardApp()
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SoundboardApp() {
    val coroutineScope = rememberCoroutineScope()
    val tabs = listOf("Tab One", "Tab Two", "Tab Three")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchText by remember { mutableStateOf("") }

    val allSounds = listOf(
        "Sound One",
        "Sound Two",
        "Sound Three",
        "Sound Four",
        "Sound Five",
        "Sound Six",
        "Sound Seven",
        "Sound Eight",
        "Sound Nine",
    )

    var filteredSounds by remember { mutableStateOf(allSounds) }
    var chunkedSounds by remember { mutableStateOf(allSounds.chunked(allSounds.size / 3)) }

    Scaffold(
        topBar = {
            Column {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        filteredSounds = filterSounds(allSounds, searchText)
                        println(filteredSounds)
                        chunkedSounds = if (filteredSounds.isNotEmpty()) {
                            filteredSounds.chunked((filteredSounds.size / 3).takeIf { it > 0 } ?: 1)
                        } else {
                            allSounds.chunked(allSounds.size / 3)
                        }
                    },
                    label = { Text("Search Sounds") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
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
        }
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

@Composable
fun TabContent(sounds: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start // Distribute buttons evenly
        ) {
            sounds.forEachIndexed { index, sound ->
                Button(onClick = {
                    /* Play the sound here */
                }) {
                    Text(sound)
                }
                if (index < sounds.size - 1) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

fun filterSounds(sounds: List<String>, searchText: String): List<String> {
    return sounds.filter { it.contains(searchText, ignoreCase = true) }
}