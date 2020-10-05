package com.distriqt.extension.inappbilling.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.adobe.fre.FREContext;
import com.distriqt.extension.inappbilling.BuildConfig;
import com.distriqt.extension.inappbilling.InAppBillingExtension;
import com.distriqt.extension.inappbilling.events.ProductViewEvent;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class ProductViewActivity extends Activity {
    public static final int REQUEST_CODE = 12342;
    public static final String TAG = ProductViewActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FREUtils.log(TAG, "onCreate()", new Object[0]);
        try {
            String packageName = getApplicationContext().getPackageName();
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("market://details?id=" + packageName));
            startActivityForResult(intent, REQUEST_CODE);
            if (InAppBillingExtension.context != null) {
                InAppBillingExtension.context.dispatchStatusEventAsync(ProductViewEvent.DISPLAYED, BuildConfig.FLAVOR);
            }
        } catch (Exception e) {
            FREUtils.handleException((FREContext) null, e);
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FREUtils.log(TAG, String.format("onActivityResult(%d,%d,...)", new Object[]{Integer.valueOf(requestCode), Integer.valueOf(resultCode)}), new Object[0]);
        if (InAppBillingExtension.context != null) {
            InAppBillingExtension.context.dispatchStatusEventAsync(ProductViewEvent.DISMISSED, BuildConfig.FLAVOR);
        }
        finish();
    }
}
