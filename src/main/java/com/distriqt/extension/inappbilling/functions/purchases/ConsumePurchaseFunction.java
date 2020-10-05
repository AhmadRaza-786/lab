package com.distriqt.extension.inappbilling.functions.purchases;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class ConsumePurchaseFunction implements FREFunction {
    public static final String TAG = ConsumePurchaseFunction.class.getSimpleName();

    public FREObject call(FREContext context, FREObject[] args) {
        FREUtils.log(TAG, "call", new Object[0]);
        try {
            InAppBillingContext ctx = (InAppBillingContext) context;
            Boolean success = false;
            if (ctx.v) {
                success = ctx.controller().consumePurchase(args[0].getProperty("productId").getAsString());
            }
            return FREObject.newObject(success.booleanValue());
        } catch (Exception e) {
            FREUtils.handleException(context, e);
            return null;
        }
    }
}
