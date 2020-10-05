package com.adobe.air;

import android.app.Activity;
import android.app.UiModeManager;
import android.os.AsyncTask;
import android.os.Build;
import com.distriqt.extension.inappbilling.BuildConfig;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public class InstallOfferPingUtils {
    private static final String LOG_TAG = "InstallOfferPingUtils";

    private static boolean isAndroidTV(Activity activity) {
        try {
            if (((UiModeManager) activity.getSystemService("uimode")).getCurrentModeType() == 4) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static void PingAndExit(Activity activity, String baseUrl, boolean installClicked, boolean update, final boolean exit) {
        String urlQueryStr;
        String urlQueryStr2;
        if (update) {
            try {
                urlQueryStr = BuildConfig.FLAVOR + "installOffer=" + (installClicked ? "ua" : "ur");
            } catch (Exception e) {
                return;
            }
        } else {
            urlQueryStr = BuildConfig.FLAVOR + "installOffer=" + (installClicked ? "a" : "r");
        }
        String urlQueryStr3 = (((urlQueryStr + "&appid=" + activity.getPackageName()) + "&runtimeType=s") + "&lang=" + Locale.getDefault().getLanguage()) + "&model=" + Build.MODEL;
        if (isAndroidTV(activity)) {
            urlQueryStr2 = urlQueryStr3 + "&os=atv";
        } else {
            urlQueryStr2 = urlQueryStr3 + "&os=a";
        }
        new AsyncTask<String, Integer, Integer>() {
            /* access modifiers changed from: protected */
            public Integer doInBackground(String... urls) {
                int resCode = 0;
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(urls[0]).openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    HttpURLConnection.setFollowRedirects(true);
                    resCode = conn.getResponseCode();
                    if (exit) {
                        System.exit(0);
                    }
                } catch (Exception e) {
                }
                return Integer.valueOf(resCode);
            }
        }.execute(new String[]{baseUrl + URLEncoder.encode((urlQueryStr2 + "&osVer=" + Build.VERSION.RELEASE) + "&arch=" + System.getProperty("os.arch"), "UTF-8")});
        activity.finish();
    }
}
