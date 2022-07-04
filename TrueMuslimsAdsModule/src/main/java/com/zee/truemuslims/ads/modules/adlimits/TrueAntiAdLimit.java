package com.zee.truemuslims.ads.modules.adlimits;

import static com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;

import timber.log.Timber;

/**
 * Created by Malik Zeeshan Habib (True Muslim) on 14,May,2022
 * https://www.truemuslimstudio.com
 */
@SuppressLint("StaticFieldLeak")
public class TrueAntiAdLimit {

    private static TrueAntiAdLimit trueAntiAdLimit;
    private Context context;

    private TrueAntiAdLimit() {
    }

    public static TrueAntiAdLimit getInstance() {
        if (trueAntiAdLimit == null)
            trueAntiAdLimit = new TrueAntiAdLimit();
        return trueAntiAdLimit;
    }

    public void init(Context context, String JSONUrl) {
        this.context = context;

        /** Start Pulling json Data in the background*/
        Intent intent = new Intent(context, TrueJSONPullService.class);
        intent.putExtra("URL", JSONUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        /*context.startService(intent);*/

        /** Display TEST Device Id*/
        new Thread(() -> {
            try {
                Timber.d("Advertising Id: %s", getAdvertisingIdInfo(getInstance().context).getId());
            } catch (GooglePlayServicesNotAvailableException ex) {
                Timber.e("Advertising Id: Google play services not available");
            } catch (GooglePlayServicesRepairableException ex) {
                Timber.e("Advertising Id: Google play services repairable");
            } catch (IOException | NullPointerException e) {
                Timber.e("Advertising Id: %s", e.getMessage());
            }
        }).start();
    }
}