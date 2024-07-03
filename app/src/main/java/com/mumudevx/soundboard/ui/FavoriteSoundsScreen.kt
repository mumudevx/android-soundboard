package com.mumudevx.soundboard.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mumudevx.soundboard.model.Sound
import com.mumudevx.soundboard.ui.theme.SoundboardTheme


class FavoriteSoundsScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundboardTheme {
                FavoriteSoundsScreenContent(null)
            }
        }
    }
}

@Composable
fun FavoriteSoundsScreenContent(favoriteSounds: List<Sound>?) {
    if (favoriteSounds.isNullOrEmpty()) {
        Text("There is no favorite sounds")
    } else {
        Text("${favoriteSounds.size} favorite sounds")
    }
}