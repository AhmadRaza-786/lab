package com.adobe.air;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;

public class AndroidNetworkDetector {
    private static final String NET_DETECT_TAG = "AndroidNetworkDetector";
    private BroadcastReceiver mReceiver;
    /* access modifiers changed from: private */
    public long objReference;
    private boolean registered = false;

    /* access modifiers changed from: private */
    public native void callOnNetworkChange(long j);

    public void RegisterForNetworkChange(Context context, long j) {
        if (!this.registered) {
            try {
                this.mReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        if (!isInitialStickyBroadcast()) {
                            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                            if (networkInfo != null) {
                                networkInfo.getDetailedState();
                            }
                            AndroidNetworkDetector.this.callOnNetworkChange(AndroidNetworkDetector.this.objReference);
                        }
                    }
                };
                this.objReference = j;
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                context.registerReceiver(this.mReceiver, intentFilter);
                this.registered = true;
            } catch (Exception e) {
            }
        }
    }
}
