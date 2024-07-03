package com.mumudevx.soundboard.model

data class Sound(val title: String, val resourceId: Int, var isFavorite: Boolean = false)