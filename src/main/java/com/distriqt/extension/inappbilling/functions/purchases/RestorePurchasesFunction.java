package com.distriqt.extension.inappbilling.functions.purchases;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class RestorePurchasesFunction implements FREFunction {
    public static String TAG = RestorePurchasesFunction.class.getSimpleName();

    public FREObject call(FREContext context, FREObject[] args) {
        FREUtils.log(TAG, "call", new Object[0]);
        try {
            InAppBillingContext ctx = (InAppBillingContext) context;
            Boolean success = false;
            if (ctx.v) {
                success = Boolean.valueOf(ctx.controller().restorePurchases());
            }
            return FREObject.newObject(success.booleanValue());
        } catch (Exception e) {
            FREUtils.handleException(context, e);
            return null;
        }
    }
}
