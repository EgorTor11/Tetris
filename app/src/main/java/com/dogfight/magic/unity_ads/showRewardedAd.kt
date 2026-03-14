package com.dogfight.magic.unity_ads

import android.app.Activity
import android.util.Log
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAds.UnityAdsShowCompletionState
import com.unity3d.ads.UnityAdsShowOptions

fun loadRewardedAd(adUnitId: String) {
    UnityAds.load(adUnitId, object : IUnityAdsLoadListener {
        override fun onUnityAdsAdLoaded(placementId: String?) {

            Log.d("UnityAds", "Ad loaded: $placementId")
        }

        override fun onUnityAdsFailedToLoad(
            placementId: String?,
            error: UnityAds.UnityAdsLoadError?,
            message: String?
        ) {
            Log.e("UnityAds", "Failed to load ad: $error — $message")
        }
    })
}

/*fun showRewardedAd(activity: Activity, adUnitId: String, onReward: () -> Unit) {
    UnityAds.show(activity, adUnitId, UnityAdsShowOptions(), object : IUnityAdsShowListener {
        override fun onUnityAdsShowComplete(
            placementId: String?,
            state: UnityAdsShowCompletionState?
        ) {
            if (state == UnityAdsShowCompletionState.COMPLETED) {
                Log.d("UnityAds", "Reward granted")
                onReward()
            } else {
                Log.d("UnityAds", "Ad not completed: $state")
            }
        }

        override fun onUnityAdsShowStart(placementId: String?) {
            Log.d("UnityAds", "Ad started")
        }

        override fun onUnityAdsShowClick(placementId: String?) {
            Log.d("UnityAds", "Ad clicked")
        }

        override fun onUnityAdsShowFailure(
            placementId: String?,
            error: UnityAds.UnityAdsShowError?,
            message: String?
        ) {
            Log.e("UnityAds", "Ad failed to show: $error — $message")
        }
    })
}*/

fun showRewardedAd(
    activity: Activity,
    adUnitId: String,
    onReward: () -> Unit,
    onFallback: () -> Unit
) {
    UnityAds.show(activity, adUnitId, UnityAdsShowOptions(), object : IUnityAdsShowListener {
        override fun onUnityAdsShowComplete(
            placementId: String?,
            state: UnityAdsShowCompletionState?
        ) {
            if (state == UnityAdsShowCompletionState.COMPLETED) {
                Log.d("UnityAds", "✅ Reward granted")
                onReward()
            } else {
                Log.w("UnityAds", "⚠️ Ad not completed: $state → fallback")
                onFallback()
            }
        }

        override fun onUnityAdsShowStart(placementId: String?) {
            Log.d("UnityAds", "🎬 Ad started")
        }

        override fun onUnityAdsShowClick(placementId: String?) {
            Log.d("UnityAds", "👆 Ad clicked")
        }

        override fun onUnityAdsShowFailure(
            placementId: String?,
            error: UnityAds.UnityAdsShowError?,
            message: String?
        ) {
            Log.e("UnityAds", "❌ Ad failed to show: $error — $message")
            onFallback()
        }
    })
}

