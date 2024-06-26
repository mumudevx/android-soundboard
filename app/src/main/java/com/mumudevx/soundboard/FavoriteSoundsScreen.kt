package com.mumudevx.soundboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun FavoriteSoundsScreen(sounds: List<Sound>) {
    val favoriteSounds = sounds.filter { it.isFavorite }

    if (favoriteSounds.isEmpty()) {
        Text("No favorite sounds yet!")
    } else {
        Text("Favorite sounds: ${favoriteSounds.size}")
    }
}