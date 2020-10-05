package com.adobe.air;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.SystemClock;
import com.adobe.air.AndroidActivityWrapper;

class ConfigDownloadListener {
    private static String LOG_TAG = "ConfigDownloadListener";
    private static ConfigDownloadListener sListener = null;
    /* access modifiers changed from: private */
    public long lastPauseTime = SystemClock.uptimeMillis();
    private AndroidActivityWrapper.StateChangeCallback mActivityStateCB = new AndroidActivityWrapper.StateChangeCallback() {
        public void onActivityStateChanged(AndroidActivityWrapper.ActivityState activityState) {
            Activity activity = AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity();
            if (activityState == AndroidActivityWrapper.ActivityState.PAUSED) {
                activity.unregisterReceiver(ConfigDownloadListener.this.mDownloadConfigRecv);
                long unused = ConfigDownloadListener.this.lastPauseTime = SystemClock.uptimeMillis();
            } else if (activityState == AndroidActivityWrapper.ActivityState.RESUMED) {
                activity.registerReceiver(ConfigDownloadListener.this.mDownloadConfigRecv, new IntentFilter(AIRService.INTENT_CONFIG_DOWNLOADED));
            }
        }

        public void onConfigurationChanged(Configuration configuration) {
        }
    };
    /* access modifiers changed from: private */
    public BroadcastReceiver mDownloadConfigRecv = new BroadcastReceiver() {
        private String LOG_TAG = "ConfigDownloadListenerBR";

        /* JADX WARNING: Code restructure failed: missing block: B:7:0x0028, code lost:
            if (com.adobe.air.ConfigDownloadListener.access$000(r6.this$0) < r2.getLong(com.adobe.air.AIRService.EXTRA_DOWNLOAD_TIME)) goto L_0x002a;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r7, android.content.Intent r8) {
            /*
                r6 = this;
                r0 = 1
                java.lang.String r1 = r8.getAction()
                java.lang.String r2 = "com.adobe.air.DownloadConfigComplete"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0035
                r1 = 0
                boolean r2 = r6.isInitialStickyBroadcast()
                if (r2 == 0) goto L_0x002a
                android.os.Bundle r2 = r8.getExtras()
                if (r2 == 0) goto L_0x0036
                java.lang.String r3 = "com.adobe.air.DownloadConfigCompleteTime"
                long r2 = r2.getLong(r3)
                com.adobe.air.ConfigDownloadListener r4 = com.adobe.air.ConfigDownloadListener.this
                long r4 = r4.lastPauseTime
                int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
                if (r2 >= 0) goto L_0x0036
            L_0x002a:
                if (r0 == 0) goto L_0x0035
                com.adobe.air.AndroidActivityWrapper r0 = com.adobe.air.AndroidActivityWrapper.GetAndroidActivityWrapper()
                if (r0 == 0) goto L_0x0035
                r0.applyDownloadedConfig()
            L_0x0035:
                return
            L_0x0036:
                r0 = r1
                goto L_0x002a
            */
            throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.ConfigDownloadListener.AnonymousClass1.onReceive(android.content.Context, android.content.Intent):void");
        }
    };

    private ConfigDownloadListener() {
        AndroidActivityWrapper.GetAndroidActivityWrapper().addActivityStateChangeListner(this.mActivityStateCB);
    }

    public static ConfigDownloadListener GetConfigDownloadListener() {
        if (sListener == null) {
            sListener = new ConfigDownloadListener();
        }
        return sListener;
    }
}
