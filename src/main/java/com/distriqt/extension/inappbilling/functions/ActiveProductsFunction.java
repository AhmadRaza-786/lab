package com.distriqt.extension.inappbilling.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.BuildConfig;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.events.InAppBillingEvent;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class ActiveProductsFunction implements FREFunction {
    private static String TAG = ActiveProductsFunction.class.getSimpleName();

    public FREObject call(FREContext context, FREObject[] args) {
        FREUtils.log(TAG, "call", new Object[0]);
        try {
            InAppBillingContext ctx = (InAppBillingContext) context;
            String productsString = BuildConfig.FLAVOR;
            if (ctx.v) {
                productsString = InAppBillingEvent.formatProductsForEvent(ctx.controller().activeProducts());
            }
            return FREObject.newObject(productsString);
        } catch (Exception e) {
            FREUtils.handleException(context, e);
            return null;
        }
    }
}
