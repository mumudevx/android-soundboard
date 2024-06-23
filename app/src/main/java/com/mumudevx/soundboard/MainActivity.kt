package com.mumudevx.soundboard

import android.content.Context
import android.media.MediaPlayer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()

        println("Reklam yükleniyor...")
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    println("Reklam yüklendi.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                    println("Reklam mesaj:" + loadAdError.message)
                }
            }
        )


        setContent {
            SoundboardApp()
        }
    }
}

@Composable
fun SoundboardApp() {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current;

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

    // Banner Ads
    val adView = remember {
        val view = AdView(context)
        view.setAdSize(AdSize.BANNER)
        view.adUnitId = "ca-app-pub-3940256099942544/9214589741"
        val adRequest = AdRequest.Builder().build()
        view.loadAd(adRequest)
        view
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = {
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

        AndroidView(
            factory = { adView },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(IntrinsicSize.Min)
        )
    }
}

@Composable
fun TabContent(sounds: List<Sound>) {
    val context = LocalContext.current;

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(16.dp),
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
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(sound.title)
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
    }
}

fun filterSounds(sounds: List<Sound>, searchText: String): List<Sound> {
    return sounds.filter { it.title.contains(searchText, ignoreCase = true) }
}

var soundCounter = 0
fun playSound(context: Context, soundId: Int) {
    val mediaPlayer = MediaPlayer.create(context, soundId)

    if (mediaPlayer.isPlaying) {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    mediaPlayer.start()

    soundCounter++
    if (soundCounter >= 10) {
        (context as MainActivity).mInterstitialAd?.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    val adRequest = AdRequest.Builder().build()
                    InterstitialAd.load(
                        context,
                        "ca-app-pub-3940256099942544/1033173712",
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                                context.mInterstitialAd = interstitialAd
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                context.mInterstitialAd = null
                            }
                        }
                    )
                }
            }
        context.mInterstitialAd?.show(context)
        soundCounter = 0
    }
}


