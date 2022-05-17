package com.truemuslin.text.adslimit.adsmodule.adlimits;

import static com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;


/**
 * Created by Malik Zeeshan Habib (True Muslim) on 14,May,2022
 * https://www.truemuslimstudio.com
 */
@SuppressLint("StaticFieldLeak")
public class TrueAntiAdLimit {

    public static final String TAG = "AntiAdLimit_TAG";
    public static final int NETWORK_FAN = 1;
    public static final int NETWORK_ADMOB = 2;
    public static final int NETWORK_NONE = 0;

    private static TrueAntiAdLimit trueAntiAdLimit;
    private Context context;

    private TrueAntiAdLimit() {

    }

    public TrueAntiAdLimit(Context context) {
        this.context = context;
    }

    public static TrueAntiAdLimit getInstance() {
        if (trueAntiAdLimit == null)
            trueAntiAdLimit = new TrueAntiAdLimit();
        return trueAntiAdLimit;
    }

    public void init(Context context, String JSONUrl) {
        this.context = context;

        // Start Pulling json Data in the background
        Intent intent = new Intent(context, TrueJSONPullService.class);
        intent.putExtra("URL", JSONUrl);
        context.startService(intent);

        // Display TEST Device Id
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Advertising Id: " + getAdvertisingIdInfo(getInstance().context).getId());
                } catch (GooglePlayServicesNotAvailableException ex) {
                    Log.e(TAG, "Advertising Id: Google play services not available");
                } catch (GooglePlayServicesRepairableException ex) {
                    Log.e(TAG, "Advertising Id: Google play services repairable");
                } catch (IOException e) {
                    Log.e(TAG, "Advertising Id: " + e.getMessage());
                } catch (NullPointerException e) {
                    Log.e(TAG, "Advertising Id: " + e.getMessage());
                }
            }
        }).start();

        // Initialize FAN
        TrueAudienceNetworkInitializeHelper.initialize(context);

        // Initialize Admob
        TrueAdmobInitializeHelper.initialize(context);
    }

    // TODO add network weight
    public int getEnabledNetwork() {
        boolean admob = TruePrefUtils.getInstance().init(context, TruePrefUtils.PREF_NAME).getAdmobActivated();
        boolean fan = TruePrefUtils.getInstance().init(context, TruePrefUtils.PREF_NAME).getFanActivated();
        if (admob)
            return NETWORK_ADMOB;
        if (fan)
            return NETWORK_FAN;
        return NETWORK_NONE;
    }
}