package com.distriqt.extension.inappbilling.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class IsProductViewSupportedFunction implements FREFunction {
    public static final String TAG = IsProductViewSupportedFunction.class.getSimpleName();

    public FREObject call(FREContext context, FREObject[] args) {
        FREUtils.log(TAG, "call", new Object[0]);
        try {
            InAppBillingContext ctx = (InAppBillingContext) context;
            boolean isSupported = false;
            if (ctx.v) {
                isSupported = ctx.controller().isProductViewSupported();
            }
            return FREObject.newObject(isSupported);
        } catch (Exception e) {
            FREUtils.handleException(context, e);
            return null;
        }
    }
}
