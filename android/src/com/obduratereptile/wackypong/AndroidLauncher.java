package com.obduratereptile.wackypong;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AndroidLauncher extends AndroidApplication implements ActionResolver {
	// AdMob ID strings:
	//  WackyPong App ID = "ca-app-pub-7651642524587492~7988629964"
	//  FirstTryInterstitialAd ID = "ca-app-pub-7651642524587492/9465363169"
	//  Dell Tablet TestDeviceID = "A0000E9FE1D80E33EF1AB393195602EB"

	//private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"; // test ads
	private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-7651642524587492/9465363169"; // live ads
	private InterstitialAd interstitialAd;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new WackyPong(this), config);

		// setup interstitial ads (we fetch a new ad whenever an ad is viewed. That way,
		// the user shouldn't have to wait for the ad to load.
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				requestNewInterstitial();
			}
		});
		requestNewInterstitial();
	}

	public void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
				.addTestDevice("A0000E9FE1D80E33EF1AB393195602EB")
				.build();

		interstitialAd.loadAd(adRequest);
	}

	public void showOrLoadInterstitialAd() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (interstitialAd.isLoaded()) {
						interstitialAd.show();
					} else {
						requestNewInterstitial();
					}
				}
			});
		} catch (Exception e) {
		}
	}
}
