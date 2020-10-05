package com.distriqt.extension.inappbilling.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class SetupFunction implements FREFunction {
    private static String TAG = SetupFunction.class.getSimpleName();

    public FREObject call(FREContext context, FREObject[] args) {
        FREUtils.log(TAG, "call", new Object[0]);
        try {
            InAppBillingContext ctx = (InAppBillingContext) context;
            boolean success = false;
            if (ctx.v) {
                success = ctx.controller().setup(args[0].getAsString()).booleanValue();
            }
            return FREObject.newObject(success);
        } catch (Exception e) {
            FREUtils.handleException(context, e);
            return null;
        }
    }
}
