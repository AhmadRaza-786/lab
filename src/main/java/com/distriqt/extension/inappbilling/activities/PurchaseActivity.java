package com.distriqt.extension.inappbilling.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.InAppBillingExtension;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class PurchaseActivity extends Activity {
    public static int PURCHASE_REQUEST_CODE = 52;
    public static String TAG = PurchaseActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FREUtils.log(TAG, "onCreate()", new Object[0]);
        setTheme(16973840);
        try {
            String productId = getIntent().getExtras().getString("productId");
            String productType = getIntent().getExtras().getString("productType");
            String payload = getIntent().getExtras().getString("payload");
            InAppBillingContext mContext = InAppBillingExtension.context;
            mContext.controller().purchaseActivity = this;
            FREUtils.log(TAG, String.format("launchPurchaseFlow( ..., %s, %s, %d, ..., %s )", new Object[]{productId, productType, Integer.valueOf(PURCHASE_REQUEST_CODE), payload}), new Object[0]);
            mContext.controller().billingHelper.flagEndAsync();
            mContext.controller().billingHelper.launchPurchaseFlow(this, productId, productType, PURCHASE_REQUEST_CODE, mContext.controller().purchaseListener, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FREUtils.log(TAG, String.format("onActivityResult(%d,%d,...)", new Object[]{Integer.valueOf(requestCode), Integer.valueOf(resultCode)}), new Object[0]);
        super.onActivityResult(requestCode, resultCode, data);
        try {
            InAppBillingExtension.context.controller().billingHelper.handleActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
        }
        if (InAppBillingExtension.context != null) {
            InAppBillingExtension.context.controller().finishActivePurchaseActivity();
        }
    }
}
