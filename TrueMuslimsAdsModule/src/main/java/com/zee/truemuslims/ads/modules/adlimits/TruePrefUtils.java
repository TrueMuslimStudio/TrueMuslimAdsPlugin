package com.zee.truemuslims.ads.modules.adlimits;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Malik Zeeshan Habib (True Muslim) on 14,May,2022
 * https://www.truemuslimstudio.com
 */

public class TruePrefUtils {

    public static final String PREF_NAME = "AntiAdLimitPref";

    /**
     * Units
     */
    private static final String LABEL_LIMIT_ACTIVATED = "JsonLimitActivated";
    private static final String LABEL_ADS_ACTIVATED = "JsonAdsActivated";
    private static final String LABEL_CLICKS = "JsonClicks";
    private static final String LABEL_IMPRESSIONS = "JsonImpressions";
    private static final String LABEL_DELAY_MS = "JsonDelayMS";
    private static final String LABEL_BAN_HOURS = "JsonBanHours";
    private static final String LABEL_HIDE_ON_CLICK = "JsonHideOnClick";

    /**
     * Networks
     */
    private static final String LABEL_NETWORK_ADMOB = "JsonNetworkAdmob";
    private static final String LABEL_NETWORK_FAN = "JsonNetworkFan";

    /**
     * STATISTICS
     */
    private static final String LABEL_COUNTER_CLICKS = "CounterClicks";
    private static final String LABEL_COUNTER_IMPRESSIONS = "CounterImpressions";
    private static final String LABEL_TIME_START_BAN = "TimeStartBan";
    private static final String LABEL_TIME_END_BAN = "TimeEndBan";

    @SuppressLint("StaticFieldLeak")
    private static TruePrefUtils truePrefUtils;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static TruePrefUtils getInstance() {
        if (truePrefUtils == null) {
            truePrefUtils = new TruePrefUtils();
        }
        return truePrefUtils;
    }

    @SuppressLint("CommitPrefEdits")
    public TruePrefUtils init(Context context, String prefName) {
        preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = preferences.edit();
        return this;
    }

    void zUpdateUnitsData(boolean limitActivated, boolean adsActivated, int clicks, int impressions, long delayMs, int banHours, boolean hideOnClick) {
        editor.putBoolean(LABEL_LIMIT_ACTIVATED, limitActivated);
        editor.putBoolean(LABEL_ADS_ACTIVATED, adsActivated);
        editor.putInt(LABEL_CLICKS, clicks);
        editor.putInt(LABEL_IMPRESSIONS, impressions);
        editor.putLong(LABEL_DELAY_MS, delayMs);
        editor.putInt(LABEL_BAN_HOURS, banHours);
        editor.putBoolean(LABEL_HIDE_ON_CLICK, hideOnClick);
        editor.apply();
    }

    void zUpdateNetworksData(boolean admob, boolean fan) {
        editor.putBoolean(LABEL_NETWORK_ADMOB, admob);
        editor.putBoolean(LABEL_NETWORK_FAN, fan);
        editor.apply();
    }

    public void zUpdateClicksCounter() {
        int currentCount = preferences.getInt(LABEL_COUNTER_CLICKS, 0) + 1;
        editor.putInt(LABEL_COUNTER_CLICKS, currentCount);
        editor.apply();
    }

    void zResetClicksCounter() {
        editor.putInt(LABEL_COUNTER_CLICKS, 0);
        editor.apply();
    }

    public void zUpdateImpressionCounter() {
        int currentCount = preferences.getInt(LABEL_COUNTER_IMPRESSIONS, 0) + 1;
        editor.putInt(LABEL_COUNTER_IMPRESSIONS, currentCount);
        editor.apply();
    }

    void zResetImpressionsCounter() {
        editor.putInt(LABEL_COUNTER_IMPRESSIONS, 0);
        editor.apply();
    }

    void zUpdateBanTime() {
        long startBan = System.currentTimeMillis();
        int banHours = preferences.getInt(LABEL_BAN_HOURS, 0);
        long hToMs = (long) banHours * 60 * 60 * 1000;
        long endBan = startBan + hToMs;
        editor.putLong(LABEL_TIME_START_BAN, startBan);
        editor.putLong(LABEL_TIME_END_BAN, endBan);
        editor.apply();
    }

    boolean getLimitActivated() {
        return preferences.getBoolean(LABEL_LIMIT_ACTIVATED, false);
    }

    boolean getAdActivated() {
        return preferences.getBoolean(LABEL_ADS_ACTIVATED, true);
    }

    public long getTimeEndBan() {
        return preferences.getLong(LABEL_TIME_END_BAN, 0);
    }

    public long getDelayMs() {
        return preferences.getLong(LABEL_DELAY_MS, 0);
    }

    public int getClicksCount() {
        return preferences.getInt(LABEL_COUNTER_CLICKS, 0);
    }

    public int getClicksLimit() {
        return preferences.getInt(LABEL_CLICKS, 0);
    }

    public int getImpressionsCount() {
        return preferences.getInt(LABEL_COUNTER_IMPRESSIONS, 0);
    }

    public int getImpressionsLimit() {
        return preferences.getInt(LABEL_IMPRESSIONS, 0);
    }

    public boolean getHideOnClick() {
        return preferences.getBoolean(LABEL_HIDE_ON_CLICK, false);
    }
}
