package com.distriqt.extension.inappbilling.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class CleanupFunction implements FREFunction {
    private static String TAG = CleanupFunction.class.getSimpleName();

    public FREObject call(FREContext context, FREObject[] args) {
        FREUtils.log(TAG, "call", new Object[0]);
        try {
            ((InAppBillingContext) context).controller().cleanup();
            return null;
        } catch (Exception e) {
            FREUtils.handleException(context, e);
            return null;
        }
    }
}
