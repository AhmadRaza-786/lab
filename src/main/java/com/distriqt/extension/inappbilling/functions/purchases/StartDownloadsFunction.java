package com.distriqt.extension.inappbilling.functions.purchases;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class StartDownloadsFunction implements FREFunction {
    public static String TAG = StartDownloadsFunction.class.getSimpleName();

    public FREObject call(FREContext context, FREObject[] args) {
        FREUtils.log(TAG, "call", new Object[0]);
        try {
            Boolean success = false;
            if (((InAppBillingContext) context).v) {
            }
            return FREObject.newObject(success.booleanValue());
        } catch (Exception e) {
            FREUtils.handleException(context, e);
            return null;
        }
    }
}
