package com.sagsaguz.appograph.utils;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.sagsaguz.appograph.R;

public class AddMob extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize the AdMob app
        MobileAds.initialize(this, getString(R.string.admob_app_id));
    }

}
