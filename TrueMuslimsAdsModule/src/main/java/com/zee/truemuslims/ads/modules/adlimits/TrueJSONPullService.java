package com.zee.truemuslims.ads.modules.adlimits;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Malik Zeeshan Habib (True Muslim) on 14,May,2022
 * https://www.truemuslimstudio.com
 */
public class TrueJSONPullService extends IntentService {

    String TAG = "JSONPullServiceClass";

    public TrueJSONPullService(String name) {
        super(name);
    }

    public TrueJSONPullService() {
        super(null);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Start Pulling JSON data");
        if (intent == null)
            return;
        String mUrl;
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;
        mUrl = extras.getString("URL");

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            String jsonData = buffer.toString();
            JSONObject o = new JSONObject(jsonData);
            JSONObject networksObject = o.getJSONObject("networks");

            boolean admobActivated = networksObject.getBoolean("admob_activated");
            boolean fanActivated = networksObject.getBoolean("fan_activated");

            TruePrefUtils.getInstance().init(getApplicationContext(), TruePrefUtils.PREF_NAME).zUpdateNetworksData(admobActivated, fanActivated);
            Log.d(TAG, "Success : ");

            JSONArray adUnitsArray = o.getJSONArray("ad_units");
            for (int i = 0; i < adUnitsArray.length(); i++) {
                JSONObject jsonObject = adUnitsArray.getJSONObject(i);
                String unitId = jsonObject.getString("unit_id");
                String adType = jsonObject.getString("ad_type");
                boolean limitActivated = jsonObject.getBoolean("limit_activated");
                boolean adActivated = jsonObject.getBoolean("ads_activated");
                int clicks = jsonObject.getInt("clicks");
                int impressions = jsonObject.getInt("impressions");
                int delayMs = jsonObject.getInt("delay_ms");
                int banHours = jsonObject.getInt("ban_hours");
                boolean hideOnClick = jsonObject.getBoolean("hide_on_click");
                Log.d(TAG, "Success : " + " Unit Is Is: " + unitId
                        + " Ad Type : " + adType + adActivated + " | " + clicks + " | " + impressions + " | " + delayMs + " | " + banHours + " | " + hideOnClick);

                /**WorkAround for creating pref xml file as it doesn't support slash symbol .. so we get the after slash only*/
                if (unitId.contains("/")) {
                    unitId = unitId.substring(unitId.lastIndexOf("/") + 1);
                }
                TruePrefUtils.getInstance().init(getApplicationContext(), unitId).zUpdateUnitsData(limitActivated, adActivated, clicks, impressions, delayMs, banHours, hideOnClick);
            }

        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (JSONException e) {

            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

}
