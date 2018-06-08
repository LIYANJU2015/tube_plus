package org.tube.player.util;

import android.os.Bundle;

import com.facebook.appevents.AppEventsLogger;

import org.tube.player.App;

/**
 * Created by liyanju on 2018/4/9.
 */

public class FacebookReport {

    public static void logSentRating(String rating) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("rating", rating);
        logger.logEvent("logRating", bundle);
    }

    public static void logSentMainPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("isFaster", App.isSuper() ? "true" : "false");
        logger.logEvent("logMainPageShow", bundle);
    }

    public static void logSentSearchPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("isFaster", App.isSuper() ? "true" : "false");
        logger.logEvent("logSearchPageShow", bundle);
    }

    public static void logSentPopupPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        logger.logEvent("logPopupPageShow");
    }

    public static void logSentBackgroudPlayerPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        logger.logEvent("logBackgroudPlayerShow");
    }

    public static void logSentDownloadPageShow() {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        logger.logEvent("logDownloadPageShow");
    }

    public static void logSentStartDownload(String title) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        logger.logEvent("logStartDownload", bundle);
    }

    public static void logSentDownloadFinish(String title) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        logger.logEvent("logDownloadFinish", bundle);
    }

    public static void logSentFBRegionOpen(String region) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("region", region);
        logger.logEvent("logSentFBRegionOpen",bundle);
    }

    public static void logSentUserInfo(String simCode, String phoneCode) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("sim_ct", simCode);
        bundle.putString("phone_ct", phoneCode);
        bundle.putString("phone", android.os.Build.MODEL);
        logger.logEvent("logSentUserInfo",bundle);
    }

    public static void logSentReferrer(String Referrer) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("referrer", Referrer);
        logger.logEvent("logSentReferrer",bundle);
    }

    public static void logSentOpenSuper(String source) {
        AppEventsLogger logger = AppEventsLogger.newLogger(App.sContext);
        Bundle bundle = new Bundle();
        bundle.putString("source", source);
        logger.logEvent("logSentOpenSuper",bundle);
    }
}
