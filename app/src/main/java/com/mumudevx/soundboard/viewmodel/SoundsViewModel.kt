package com.mumudevx.soundboard.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mumudevx.soundboard.R
import com.mumudevx.soundboard.model.Sound

class SoundsViewModel(context: Context) : ViewModel() {
    private val _favoriteSounds = MutableLiveData<List<Sound>>(listOf())

    val favoriteSounds: LiveData<List<Sound>> = _favoriteSounds
    val favoriteSoundsCount = MutableLiveData<Int>()

    private val sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE)

    init {
        loadFavorites()

        _favoriteSounds.observeForever {
            favoriteSoundsCount.value = it.size
        }
    }

    private fun loadFavorites() {
        val favoritesSet = sharedPreferences.getStringSet("favoriteIndexes", setOf()) ?: setOf()
        val favoritesList = favoritesSet.map { index -> allSounds[index.toInt()] }
        _favoriteSounds.value = favoritesList
    }

    @SuppressLint("MutatingSharedPrefs")
    fun addFavorite(sound: Sound) {
        val index = allSounds.indexOf(sound)
        if (index != -1) {
            val editor = sharedPreferences.edit()
            val favoritesSet =
                sharedPreferences.getStringSet("favoriteIndexes", mutableSetOf()) ?: mutableSetOf()
            favoritesSet.add(index.toString())
            editor.putStringSet("favoriteIndexes", favoritesSet)
            editor.apply()

            _favoriteSounds.value = _favoriteSounds.value?.plus(sound)

            println("Favorite sounds count: ${favoriteSoundsCount.value}")
            println("Favorite sounds: ${favoriteSounds.value}")
        }
    }

    @SuppressLint("MutatingSharedPrefs")
    fun removeFavorite(sound: Sound) {
        val index = allSounds.indexOf(sound)
        if (index != -1) {
            val editor = sharedPreferences.edit()
            val favoritesSet =
                sharedPreferences.getStringSet("favoriteIndexes", mutableSetOf()) ?: mutableSetOf()
            favoritesSet.remove(index.toString())
            editor.putStringSet("favoriteIndexes", favoritesSet)
            editor.apply()

            _favoriteSounds.value = _favoriteSounds.value?.filter { it != sound }
        }
    }

    companion object {
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
    }
}