package com.adobe.air;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class AndroidGcmResultReceiver extends ResultReceiver {
    private Receiver mReceiver = null;

    public interface Receiver {
        void onReceiveResult(int i, Bundle bundle);
    }

    public AndroidGcmResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.mReceiver = receiver;
    }

    /* access modifiers changed from: protected */
    public void onReceiveResult(int i, Bundle bundle) {
        if (this.mReceiver != null) {
            this.mReceiver.onReceiveResult(i, bundle);
        }
    }
}
