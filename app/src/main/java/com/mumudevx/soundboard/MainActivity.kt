package com.mumudevx.soundboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.MobileAds
import com.mumudevx.soundboard.ui.FavoriteSoundsScreenContent
import com.mumudevx.soundboard.ui.SoundboardScreenContent
import com.mumudevx.soundboard.ui.theme.SoundboardTheme

class MainActivity : ComponentActivity() {
    private var mInterstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Admob Interstitial Ad
        MobileAds.initialize(this) {}
        loadInterstitialAd()

        // Admob Banner Ad
        val adRequestForBanner = AdRequest.Builder().build()
        val adView = AdView(this)
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = getString(R.string.admob_banner_ad_id)
        adView.loadAd(adRequestForBanner)

        setContent {
            SoundboardTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeContentPadding()
                ) {
                    val navController = rememberNavController();
                    NavHost(navController = navController, startDestination = "soundboard") {
                        composable("soundboard") {
                            SoundboardScreenContent(navController)
                        }
                        composable("favoriteSounds") {
                            FavoriteSoundsScreenContent(navController)
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
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            getString(R.string.admob_interstitial_ad_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd

                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                    println(loadAdError.message)
                }
            }
        )
    }

    fun showInterstitialAd() {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                loadInterstitialAd()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                println(adError.message)
                loadInterstitialAd()
            }

            override fun onAdShowedFullScreenContent() {
                mInterstitialAd = null
            }
        }

        mInterstitialAd?.show(this)
    }
}