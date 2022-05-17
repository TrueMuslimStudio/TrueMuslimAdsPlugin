package com.truemuslin.text.adslimit.adsmodule.adlimits;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

/**
 * Created by Malik Zeeshan Habib (True Muslim) on 14,May,2022
 * https://www.truemuslimstudio.com
 */
public class TrueAdmobInitializeHelper {

    public static void initialize(Context context) {
        String TAG = "AdmobInitializeHelperClass";
        MobileAds.initialize(context, initializationStatus -> {
        });
    }
}
