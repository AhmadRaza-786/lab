package com.adobe.air.telephony;

import android.telephony.PhoneStateListener;

/* compiled from: AndroidTelephonyManager */
class AndroidPhoneStateListener extends PhoneStateListener {
    private native void nativeOnCallStateChanged(int i);

    AndroidPhoneStateListener() {
    }

    public void onCallStateChanged(int i, String str) {
        nativeOnCallStateChanged(toAIRCallState(i));
    }

    private int toAIRCallState(int i) {
        if ((i & 1) == 1) {
            return 1;
        }
        if ((i & 2) == 2) {
            return 2;
        }
        return 0;
    }
}
