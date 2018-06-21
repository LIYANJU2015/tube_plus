package com.leiting.mission.util;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import org.tube.player.App;
import org.tube.player.BuildConfig;
import org.tube.player.util.FBAdUtils;
import org.tube.player.util.FacebookReport;
import org.tube.player.util.Utils;

import java.util.Locale;

/**
 * Created by liyanju on 2018/4/3.
 */

public class ReferVersions {


    public static void setSuper() {
        SuperVersionHandler.get().setSuper();
    }

    public static void initSuper() {
        SuperVersionHandler.get().initSpecial();
    }

    public static boolean isSuper() {
        return SuperVersionHandler.get().isSpecial();
    }

    public static class SuperVersionHandler {

        private static volatile SuperVersionHandler superVersionHandler;

        public static SuperVersionHandler get() {
            if (superVersionHandler == null) {
                synchronized (SuperVersionHandler.class) {
                    if (superVersionHandler == null) {
                        superVersionHandler = new SuperVersionHandler();
                    }
                }
            }
            return superVersionHandler;
        }

        private volatile boolean isSpecial = false;

        private volatile boolean isShowPlayAd = false;

        public static final String KEY_SPECIAL = "fastershowapp";

        public void setSuper() {
            isSpecial = true;
            App.sPreferences.edit().putBoolean(KEY_SPECIAL, true).apply();
            setShowPlayAd();
        }

        public void setShowPlayAd() {
            isShowPlayAd = true;
            App.sPreferences.edit().putBoolean("playshowad", true).apply();
        }

        public String getPhoneCountry(Context context) {
            String country = "";
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager.getPhoneType()
                        != TelephonyManager.PHONE_TYPE_CDMA) {
                    country = telephonyManager.getNetworkCountryIso();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return country;
        }

        public String getCountry2(Context context) {
            String country = "";
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String simCountry = telephonyManager.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2) {
                    country = simCountry.toUpperCase(Locale.ENGLISH);
                } else if (telephonyManager.getPhoneType()
                        != TelephonyManager.PHONE_TYPE_CDMA) {
                    country = telephonyManager.getNetworkCountryIso();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return country;
        }

        public String getSimCountry(Context context) {
            String country = "";
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String simCountry = telephonyManager.getSimCountryIso();
                if (simCountry != null && simCountry.length() == 2) {
                    country = simCountry.toUpperCase(Locale.ENGLISH);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return country;
        }

        public void initSpecial() {
            isSpecial = App.sPreferences.getBoolean(KEY_SPECIAL, false);
            isShowPlayAd = App.sPreferences.getBoolean("playshowad", false);
        }

        public boolean isSpecial() {
            return isSpecial;
        }

        public boolean isIsBGPlayer() {
            return isSpecial;
        }

        public boolean isIsShowPlayAd() {
            return isShowPlayAd;
        }

        public static boolean isReferrerOpen(String referrer) {
            if (referrer.startsWith("campaigntype=")
                    && referrer.contains("campaignid=")) {
                return true;
            } else {
                return false;
            }
        }

        private static boolean countryIfShow2(String country) {
            if ("mx".equals(country.toLowerCase())) {
                return true;
            }

            if ("id".equals(country.toLowerCase())) {
                return true;
            }

            if ("ph".equals(country.toLowerCase())) {
                return true;
            }

            if ("it".equals(country.toLowerCase())) {
                return true;
            }

            if ("de".equals(country.toLowerCase())) {
                return true;
            }

            if ("gb".equals(country.toLowerCase())) {
                return true;
            }

            if ("fr".equals(country.toLowerCase())) {
                return true;
            }

            return false;
        }

        private static boolean countryIfShow(String country) {

            if ("id".equals(country.toLowerCase())) {
                FacebookReport.logSentFBRegionOpen("id");
                return true;
            }

            if ("br".equals(country.toLowerCase())) {
                FacebookReport.logSentFBRegionOpen("br");
                return true;
            }

            if ("th".equals(country.toLowerCase())) {
                FacebookReport.logSentFBRegionOpen("th");
                return true;
            }

            if ("vn".equals(country.toLowerCase())) {
                FacebookReport.logSentFBRegionOpen("vn");
                return true;
            }

            if ("sa".equals(country.toLowerCase())) {
                FacebookReport.logSentFBRegionOpen("sa");
                return true;
            }

            return false;
        }


        public void countryIfShow(Context context) {
            String country4 = getPhoneCountry(context);
            String country = getCountry2(context);
            String country3 = getSimCountry(context);

            if (TextUtils.isEmpty(country)) {
                return;
            }

            if (!TextUtils.isEmpty(country4)
                    && !TextUtils.isEmpty(country3)
                    && !country4.toLowerCase().equals(country3.toLowerCase())
                    && Utils.isRoot()) {
                return;
            }

            if (countryIfShow(country)) {
                setSuper();
                FacebookReport.logSentOpenSuper("nation open");
                return;
            }

            if (countryIfShow2(country)) {
                setShowPlayAd();
                return;
            }
        }
    }

    public static class AppLinkDataHandler {

//        public static void fetchDeferredAppLinkData(Context context) {
//            int count = App.sPreferences.getInt("fetchcount2", 0);
//            if (count < 2) {
//                count++;
//                App.sPreferences.getInt("fetchcount2", count);
//                AppLinkData.fetchDeferredAppLinkData(context, context.getString(R.string.facebook_app_id),
//                        new AppLinkData.CompletionHandler() {
//                            @Override
//                            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
//                                Log.v("xx", " onDeferredAppLinkDataFetched>>>>");
//                                if (appLinkData != null && appLinkData.getTargetUri() != null) {
//                                    Log.v("xx", " onDeferredAppLinkDataFetched111>>>>");
//                                    String deepLinkStr = appLinkData.getTargetUri().toString();
//                                    FacebookReport.logSentFBDeepLink(deepLinkStr);
//                                    if (App.DEEPLINK.equals(deepLinkStr)) {
//                                        FacebookReport.logSentOpenSuper("facebook");
//                                        App.setSuper();
//                                    }
//                                }
//                                App.sPreferences.edit().putInt("fetchcount2", 2).apply();
//                            }
//                        });
//            }
//        }
    }


    public static void onHandleIntent(Context context, Intent intent) {
        String referrer = intent.getStringExtra("referrer");
        if (referrer == null) {
            return;
        }

        boolean result = App.sPreferences.getBoolean("logreferrer", false);
        if (result) {
            return;
        }
        App.sPreferences.edit().putBoolean("logreferrer", true).apply();

        if (BuildConfig.DEBUG) {
            Log.e("referrer:::::", referrer);
        } else {
            if (!App.sPreferences.getBoolean("canRefer", true)) {
                Log.e("referrer", "canRefer false ");
                return;
            }
        }

        FacebookReport.logSentReferrer(referrer);

        if (FBAdUtils.isReferrerOpen(referrer)) {
            if (BuildConfig.DEBUG) {
                Log.v("faster", "fasterOpen true");
            }
            FacebookReport.logSentOpenSuper("from admob");
            setSuper();
        } else {
            SuperVersionHandler.get().countryIfShow(context);
        }

        FacebookReport.logSentUserInfo(SuperVersionHandler.get().getSimCountry(context),
                SuperVersionHandler.get().getPhoneCountry(context));
    }

}
