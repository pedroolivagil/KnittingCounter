package com.olivadevelop.knittingcounter.tools;

import android.app.Activity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class ADSAdmob {
    private Activity activity;
    private InterstitialAd mInterstitialAd;

    public ADSAdmob(Activity activity) {
        this.activity = activity;
//        init();
    }

    private void init() {
        MobileAds.initialize(this.activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadIntersticial();
    }

    private void loadIntersticial() {
        mInterstitialAd = new InterstitialAd(this.activity);
        mInterstitialAd.setAdUnitId("ca-app-pub-2083012446340886/3207500464");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
    }

    public void showInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
}
