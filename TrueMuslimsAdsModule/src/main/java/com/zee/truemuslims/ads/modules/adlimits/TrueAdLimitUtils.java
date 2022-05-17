package com.zee.truemuslims.ads.modules.adlimits;

import android.content.Context;
import android.util.Log;

/**
 * Created by Malik Zeeshan Habib (True Muslim) on 14,May,2022
 * https://www.truemuslimstudio.com
 */
public class TrueAdLimitUtils {

    public static String adUnitId;
    public static String adsType;

    public static boolean isBanned(Context context, String unitId, String adType) {
        adUnitId = unitId;
        adsType = adType;
        boolean limitActivated = TruePrefUtils.getInstance().init(context, unitId).getLimitActivated();
        boolean adActivated = TruePrefUtils.getInstance().init(context, unitId).getAdActivated();
        int clicksLimit = TruePrefUtils.getInstance().init(context, unitId).getClicksLimit();
        int impressionsLimit = TruePrefUtils.getInstance().init(context, unitId).getImpressionsLimit();
        int clicksCount = TruePrefUtils.getInstance().init(context, unitId).getClicksCount();
        int impressionsCount = TruePrefUtils.getInstance().init(context, unitId).getImpressionsCount();
        long timeEndBan = TruePrefUtils.getInstance().init(context, unitId).getTimeEndBan();
        Log.d("TrueAdLimitUtilsClass", "isBanned: " + unitId + " Click Limits : " + clicksLimit + " Impression Limit :" + impressionsLimit);

        /**If Limit Activated*/
        if (!limitActivated)
            return false;

        /**If ad deactivated*/
        if (!adActivated)
            return true;

        /**If limit is 0 or not defined will show ads anyway*/
        if (clicksLimit == 0 || impressionsLimit == 0)
            return false;
        /**If unit is banned from showing ad for a period of time*/
        if (System.currentTimeMillis() < timeEndBan)
            return true;


        boolean banned = false;
        if (clicksCount >= clicksLimit || impressionsCount >= impressionsLimit) {
            TruePrefUtils.getInstance().init(context, unitId).zResetClicksCounter();
            TruePrefUtils.getInstance().init(context, unitId).zResetImpressionsCounter();
            TruePrefUtils.getInstance().init(context, unitId).zUpdateBanTime();
            banned = true;
        }
        return banned;

    }

}
