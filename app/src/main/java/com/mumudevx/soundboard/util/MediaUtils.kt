package com.mumudevx.soundboard.util

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mumudevx.soundboard.R
import java.io.File
import java.io.FileOutputStream
import android.media.RingtoneManager

object MediaUtils {
    fun setSoundAs(context: Context, soundId: Int, soundTitle: String, soundType: Int) {
        // Check if we have the necessary permissions
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_SETTINGS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions if necessary
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_SETTINGS
                ),
                0
            )
            return
        }

        val contentResolver = context.contentResolver
        val file =
            File(context.getExternalFilesDir(Environment.DIRECTORY_RINGTONES), "$soundTitle.mp3")

        try {
            val inputStream = context.resources.openRawResource(soundId)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DATA, file.absolutePath)
                put(MediaStore.MediaColumns.TITLE, soundTitle)
                put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
                put(MediaStore.MediaColumns.SIZE, file.length())
                put(MediaStore.Audio.Media.ARTIST, R.string.app_name)

                when (soundType) {
                    RingtoneManager.TYPE_RINGTONE -> {
                        put(MediaStore.Audio.Media.IS_RINGTONE, true)
                    }
                    RingtoneManager.TYPE_NOTIFICATION -> {
                        put(MediaStore.Audio.Media.IS_NOTIFICATION, true)
                    }
                    RingtoneManager.TYPE_ALARM -> {
                        put(MediaStore.Audio.Media.IS_ALARM, true)
                    }
                }
            }

            // Use the MediaStore.Audio.Media.EXTERNAL_CONTENT_URI as the base URI
            val newUri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)

            if (newUri != null) {
                RingtoneManager.setActualDefaultRingtoneUri(
                    context,
                    RingtoneManager.TYPE_RINGTONE,
                    newUri
                )
                Toast.makeText(context, "Ringtone set successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to set ringtone", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to set ringtone", Toast.LENGTH_SHORT).show()
        }
    }
}