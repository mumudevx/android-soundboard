package com.mumudevx.soundboard.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mumudevx.soundboard.viewmodel.SoundsViewModel

class FavoriteSoundsScreen : ComponentActivity()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteSoundsScreenContent(navController: NavController) {
    //val viewModel = SoundsViewModel(LocalContext.current)

    //val favoriteSounds by viewModel.favoriteSounds.observeAsState(initial = listOf())

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
                // Display favorite sounds
                /*favoriteSounds.forEach { sound ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = sound.title)
                        // Add more UI elements as needed
                    }
                }*/
            }
        }
    }
}