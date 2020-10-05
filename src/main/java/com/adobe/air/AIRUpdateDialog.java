package com.adobe.air;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import com.adobe.air.AndroidLocale;

public class AIRUpdateDialog extends Activity {
    /* access modifiers changed from: private */
    public static String AIR_PING_URL = "https://airdownload2.adobe.com/air?";
    /* access modifiers changed from: private */
    public static AIRUpdateDialog sThis = null;
    private final String LOG_TAG = "AIRUpdateDialog";
    private final String RUNTIME_PACKAGE_ID = "com.adobe.air";
    /* access modifiers changed from: private */
    public AndroidActivityWrapper actWrapper;
    private AlertDialog alertDialog = null;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        sThis = this;
        this.actWrapper = AndroidActivityWrapper.CreateAndroidActivityWrapper(this, false);
        this.alertDialog = new AlertDialog.Builder(this).setTitle(AndroidConstants.ADOBE_AIR).setMessage(AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_RUNTIME_UPDATE_MESSAGE)).create();
        this.alertDialog.setButton(-1, AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_UPDATE), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AIRUpdateDialog.this.actWrapper.LaunchMarketPlaceForAIR(AIRUpdateDialog.this.getIntent().getStringExtra("airDownloadURL"));
                InstallOfferPingUtils.PingAndExit(AIRUpdateDialog.sThis, AIRUpdateDialog.AIR_PING_URL, true, true, false);
            }
        });
        this.alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i != 4 && i != 84) {
                    return false;
                }
                dialogInterface.cancel();
                InstallOfferPingUtils.PingAndExit(AIRUpdateDialog.sThis, AIRUpdateDialog.AIR_PING_URL, false, true, false);
                return true;
            }
        });
        this.alertDialog.setCancelable(true);
        this.alertDialog.show();
    }

    public void onPause() {
        if (this.alertDialog != null) {
            this.alertDialog.cancel();
            this.alertDialog = null;
            finish();
        }
        super.onPause();
    }

    public void onStop() {
        if (this.alertDialog != null) {
            this.alertDialog.cancel();
            this.alertDialog = null;
            finish();
        }
        super.onStop();
    }
}
